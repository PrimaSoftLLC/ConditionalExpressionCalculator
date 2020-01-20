package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;
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
     * calculate value and apply condition
     */
    public static float calculateValue(SensorI sensor, MessageI m, Map<Long, SensorI> sensorMap) {
        float v = calculateWithoutCondition(sensor, m, sensorMap);
        if (!applyCondition(sensor, m, v, sensorMap)) {
            v = Float.NaN;
        }
        return v;
    }

    /**
     * calculate value without applying condition
     */
    public static float calculateWithoutCondition(SensorI sensor, MessageI m, Map<Long, SensorI> sensorMap) {
        buildFormula(sensor, sensorMap);
        float v = getValueByName(sensor, m.getParams());
        if (sensor.getFormulaRaw() != null && sensor.getFormula().size() > 0) {
            replaceTokens(sensorMap, sensor.getFormula(), v, m);
            v = convertCalibration(sensor, ExpressionCalculator.calculateMath(sensor.getFormula()));
        } else {
            v = convertCalibration(sensor, v);
        }
        return v;
    }

    /**
     * apply condition from this object {@prop condition}
     */
    public static boolean applyCondition(SensorI sensor, MessageI m, float v, Map<Long, SensorI> sensorMap) {
        buildCondition(sensor, sensorMap);
        if (sensor.getConditionRaw() != null && sensor.getCondition().size() > 0) {
            replaceTokens(sensorMap, sensor.getCondition(), v, m);
            return ExpressionCalculator.calculateConditional(sensor.getCondition());
        }
        return true;
    }

    /**
     * apply external condition {@prop condition} for this sensor
     */
    public static boolean applyCondition(SensorI sensor, List<EToken> c, MessageI m, Map<Long, SensorI> mapSensors) {
        if (c == null || c.size() == 0) {
            return true;
        }
        float value = calculateValue(sensor, m, mapSensors);
        replaceTokens(mapSensors, c, value, m);
        return ExpressionCalculator.calculateConditional(c);
    }

    private static float getValueByName(SensorI sensor, String raw) {
        try {
            if (sensor.getParameter() == null) {
                return 0;
            }
            String name = sensor.getParameter().getName() + ':';
            int start = raw.indexOf(name);
            if (start == -1) {
                return -1;
            }
            start = start + name.length() + 2;
            int end = raw.indexOf(',', start);
            if (end == -1) {
                return Float.valueOf(raw.substring(start));
            } else {
                return Float.valueOf(raw.substring(start, end));
            }
        } catch (Exception e) {
            return -999;
        }
    }

    private static float convertCalibration(SensorI sensor, float value) {
        return sensor.getCalibration().calculateCalibrationValue(value);
    }

    /**
     * prepare {@param tokens} to calculate into {@link ExpressionCalculator}
     */
    private static void replaceTokens(Map<Long, SensorI> sensorsMap, List<EToken> tokens, float value, MessageI m) {
        for (EToken t : tokens) {
            if(t.is(EType.PARAM)){
                t.setValue(m.getFieldValue(t.getParamName()));
            }
            if (t.is(EType.TOKEN)) {
                if (t.getTokenId() == 0) {
                    t.setValue(value);
                } else {
                    SensorI s = sensorsMap.get(t.getTokenId());
                    t.setValue(SensorCalculator.calculateValue(s, m, sensorsMap));
                }
            }
        }
    }

    public static List<EToken> buildCondition(SensorI sensor, Map<Long, SensorI> sensorMap) {
        try {
            if (sensor.getConditionRaw() != null && sensor.getCondition() == null) {
                sensor.setCondition(ReversePolishNotation.create(sensor.getConditionRaw(), sensorMap.values()));
            }
        } catch (Exception e){
         log.error("Sensor: '{}' condition: '{}' can not be build. Message: '{}'", sensor.getId(), sensor.getConditionRaw(), e.getMessage());
            sensor.setCondition(List.of());
        }
        return sensor.getCondition();
    }

    public static List<EToken> buildFormula(SensorI sensor, Map<Long, SensorI> sensorMap) {
        try {
            if (sensor.getFormulaRaw() != null && sensor.getFormula() == null) {
                sensor.setFormula(ReversePolishNotation.create(sensor.getFormulaRaw(), sensorMap.values()));
            }
        } catch (Exception e){
            log.error("Sensor: '{}' formula: '{}' can not be build. Message: '{}'", sensor.getId(), sensor.getFormulaRaw(), e.getMessage());
            sensor.setFormula(List.of());
        }
        return sensor.getFormula();
    }
}
