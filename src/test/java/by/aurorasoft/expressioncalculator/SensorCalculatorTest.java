package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.sensor.MessageI;

import by.aurorasoft.expressioncalculator.model.sensor.SensorI;
import by.aurorasoft.expressioncalculator.model.sensor.impl.*;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SensorCalculatorTest {

    @Test
    public void calculateValueShouldReturnValueFromMessageByParamNameLengthOne() {
        SensorImpl givenSensor = buildSensor(1l, "test", "param1", new Calibration(), null, null, Map.of());

        MessageI m1 = buildMessage(1l, "2020-01-20T10:00:00Z", "param1:1:100");

        Map<Long, SensorImpl> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageByParamName() {
        SensorI givenSensor = buildSensor(1l, "test", "param2", new Calibration(), null, null, Map.of());

        MessageI m1 = buildMessage(1l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(200, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageByParamNameLast() {
        SensorI givenSensor = buildSensor(1l, "test", "param3", new Calibration(), null, null, Map.of());

        MessageI m1 = buildMessage(1l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageConvertedWithCalibrationTable() {
        List<RawData> rawCalibration = List.of(new RawData(100, 200, ""), new RawData(200, 300, ""));
        SensorI givenSensor = buildSensor(1l, "test", "param1", new Calibration(rawCalibration, List.of()), null, null, Map.of());

        MessageI m1 = buildMessage(1l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");
        MessageI m2 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:150,param2:1:200,param3:1:300");
        MessageI m3 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:200,param2:1:200,param3:1:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(200, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
        assertEquals(250, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m3, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageWithFormula() {
        SensorI givenSensor = buildSensor(1l, "test", "param1", new Calibration(), "{x}+99", null, Map.of());

        MessageI m1 = buildMessage(1l, "2020-01-20T10:00:00Z", "param1:1:0,param2:1:200,param3:1:300");
        MessageI m2 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");
        MessageI m3 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:200,param2:1:200,param3:1:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(99, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
        assertEquals(199, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
        assertEquals(299, SensorCalculator.calculateValue(givenSensor, m3, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageWithFormulaAndTwoSensors() {
        SensorI s1 = buildSensor(2l, "test2", "param2", new Calibration(), "{x}+1", null, Map.of());
        SensorI givenSensor = buildSensor(1l, "test", "param1", new Calibration(), "{x}+{test2}+99", null, Map.of());

        MessageI m1 = buildMessage(1l, "2020-01-20T10:00:00Z", "param1:1:0,param2:1:200,param3:1:300");
        MessageI m2 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");
        MessageI m3 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:200,param2:1:200,param3:1:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor, s1.getId(), s1);

        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
        assertEquals(400, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
        assertEquals(500, SensorCalculator.calculateValue(givenSensor, m3, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageWithFormulaWhenIllegalSensorName() {

        SensorI givenSensor = buildSensor(1l, "test", "param1", new Calibration(), "({x}+{test2})+99", null, Map.of());

        MessageI m2 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameter() {

        SensorI givenSensor = buildSensor(1l, "test", "param1", new Calibration(), "{#speed}", null, Map.of());

        MessageI m2 = new MessageImpl(2l, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, "param1:1:100,param2:1:200,param3:1:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(123, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnMinus999999IfUnknownParameter() {

        SensorI givenSensor = buildSensor(1l, "test", "null", new Calibration(), "{#unknown}", null, Map.of());

        MessageI m2 = new MessageImpl(2l, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, "param1:1:100,param2:1:200,param3:1:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(0, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnNaN() {

        SensorI givenSensor = buildSensor(1l, "test", "param2", new Calibration(), null, "{x}=0", Map.of());

        MessageI m2 = buildMessage(2l, "2020-01-20T10:00:00Z", "param1:1:100,param2:1:200,param3:1:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    private SensorImpl buildSensor(Long id, String name, String parameter, Calibration calibration, String formulaRaw, String conditionRaw, Map<String, String> options) {
        return new SensorImpl(id, name, 123l,
                parameter.equalsIgnoreCase("null") ? null : new ParameterImpl(23l, parameter),
                calibration, formulaRaw, conditionRaw, options);
    }

    private MessageI buildMessage(Long id, String datetime, String params) {
        return new MessageImpl(id, Instant.parse(datetime), 0, 0, 0, 0, 0, 0, params);
    }
}
