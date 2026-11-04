package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.MessageI;
import by.aurorasoft.expressioncalculator.model.sensor.SensorI;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * if formula contains illegal or not exist sensor name formula ignored and return value from parameter
 */
@Slf4j
public class SensorCalculator {

    /**
     * Compile a complete snapshot before publishing it to worker threads.
     * Configuration and tokens must remain unchanged during calculation. Calls without
     * preparation compile locally and do not cache expressions in the sensor.
     */
    public static void prepare(Map<Long, ? extends SensorI> sensorMap) {
        for (SensorI sensor : sensorMap.values()) {
            buildFormula(sensor, sensorMap);
            buildCondition(sensor, sensorMap);
        }
    }

    /**
     * calculate value and apply condition
     */
    public static double calculateValue(SensorI sensor, MessageI m, Map<Long, ? extends SensorI> sensorMap) {
        double v = calculateWithoutCondition(sensor, m, sensorMap);
        if (!applyCondition(sensor, m, v, sensorMap)) {
            v = Double.NaN;
        }
        return v;
    }

    /**
     * calculate value without applying condition
     */
    public static double calculateWithoutCondition(SensorI sensor, MessageI m, Map<Long, ? extends SensorI> sensorMap) {
        List<EToken> tokens = expression(sensor.getFormulaRaw(), sensor.getFormula(), sensor, sensorMap, "formula");
        double v = 0;
        if (sensor.getFormulaRaw() != null && !tokens.isEmpty()) {
            v = convertCalibration(sensor, ExpressionCalculator.calculateMath(tokens, m, 0, sensorMap));
        } else {
            v = convertCalibration(sensor, v);
        }
        return v;
    }

    /**
     * apply condition from this object {@prop condition}
     */
    public static boolean applyCondition(SensorI sensor, MessageI m, double v, Map<Long, ? extends SensorI> sensorMap) {
        List<EToken> tokens = expression(sensor.getConditionRaw(), sensor.getCondition(), sensor, sensorMap, "condition");
        if (sensor.getConditionRaw() != null && !tokens.isEmpty()) {
            return ExpressionCalculator.calculateConditional(tokens, m, v, sensorMap);
        }
        return true;
    }

    /**
     * apply external condition {@prop condition} for this sensor
     */
    public static boolean applyCondition(SensorI sensor, List<EToken> c, MessageI m, Map<Long, ? extends SensorI> sensorMap) {
        if (c == null || c.size() == 0) {
            return true;
        }
        double value = calculateValue(sensor, m, sensorMap);
        return ExpressionCalculator.calculateConditional(c, m, value, sensorMap);
    }

    /**
     * get message parameter value by name
     */
    public static double getValueByName(String parameterName, MessageI m) {
        if (parameterName == null || m == null) {
            return Double.NaN;
        }
        return m.getParameterValue(parameterName);
    }

    private static double convertCalibration(SensorI sensor, double value) {
        return sensor.getCalibration().calculateCalibrationValue(value);
    }

    private static List<EToken> expression(String raw, List<EToken> prepared, SensorI sensor,
                                          Map<Long, ? extends SensorI> map, String kind) {
        if (prepared != null) return prepared;
        if (raw == null) return List.of();
        try {
            return List.copyOf(ReversePolishNotation.create(raw, map.values()));
        } catch (Exception e) {
            log.error("Sensor: '{}' {}: '{}' can not be build. Message: '{}'", sensor.getId(), kind, raw, e.getMessage());
            return List.of();
        }
    }

    public static List<EToken> buildCondition(SensorI sensor, Map<Long, ? extends SensorI> sensorMap) {
        try {
            if (sensor.getConditionRaw() != null && sensor.getCondition() == null) {
                sensor.setCondition(expression(sensor.getConditionRaw(), null, sensor, sensorMap, "condition"));
            }
        } catch (Exception e) {
            log.error("Sensor: '{}' condition: '{}' can not be build. Message: '{}'", sensor.getId(), sensor.getConditionRaw(), e.getMessage());
            sensor.setCondition(List.of());
        }
        return sensor.getCondition();
    }

    public static List<EToken> buildFormula(SensorI sensor, Map<Long, ? extends SensorI> sensorMap) {
        try {
            if (sensor.getFormulaRaw() != null && sensor.getFormula() == null) {
                List<EToken> reversePolishNotation = expression(sensor.getFormulaRaw(), null, sensor, sensorMap, "formula");
                sensor.setFormula(reversePolishNotation);
            }
        } catch (Exception e) {
            log.error("Sensor: '{}' formula: '{}' can not be build. Message: '{}'", sensor.getId(), sensor.getFormulaRaw(), e.getMessage());
            sensor.setFormula(List.of());
        }
        return sensor.getFormula();
    }
}
