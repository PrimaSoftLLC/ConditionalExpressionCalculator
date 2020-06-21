package by.aurorasoft.expressioncalculator.model.sensor.impl;

import by.aurorasoft.expressioncalculator.SensorCalculator;
import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.MessageI;
import by.aurorasoft.expressioncalculator.model.sensor.ParameterI;
import by.aurorasoft.expressioncalculator.model.sensor.SensorI;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SensorImpl implements SensorI {
    private final Long id;
    private final String name;
    private final Long unitId;
    private final ParameterI parameter;
    private final Calibration calibration;
    private final String formulaRaw;
    private final String conditionRaw;
    private List<EToken> formula;
    private List<EToken> condition;
    private final Map<String, String> options;

    public SensorImpl(Long id, String name, Long unitId, ParameterI parameter, Calibration calibration, String formulaRaw, String conditionRaw, Map<String, String> options) {
        this.id = id;
        this.name = name;
        this.unitId = unitId;
        this.parameter = parameter;
        this.calibration = calibration;
        this.formulaRaw = formulaRaw;
        this.conditionRaw = conditionRaw;
        this.options = options;
    }

    @Override
    public double calculateValue(MessageI m, Map<Long, ? extends SensorI> sensorMap) {
        return SensorCalculator.calculateValue(this, m, sensorMap);
    }
}
