package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.sensor.MessageI;
import by.aurorasoft.expressioncalculator.model.sensor.SensorI;
import by.aurorasoft.expressioncalculator.model.sensor.impl.*;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SensorCalculatorTest {

    @Test
    public void calculateValueShouldReturnValueFromMessageByParamNameLengthOne() {
        SensorImpl givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}", null, Map.of());

        MessageI m1 = buildMessage(1L, "param1:100");

        Map<Long, SensorImpl> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageByParamName() {
        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param2}", null, Map.of());

        MessageI m1 = buildMessage(1L, "param1:100,param2:200,param3:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(200, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageByParamNameLast() {
        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param3}", null, Map.of());

        MessageI m1 = buildMessage(1L, "param1:100,param2:200,param3:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageConvertedWithCalibrationTable() {
        List<RawData> rawCalibration = List.of(new RawData(100, 200, ""), new RawData(200, 300, ""));
        SensorI givenSensor = buildSensor(1L, "test", new Calibration(rawCalibration, List.of()), "{@param1}", null, Map.of());

        MessageI m1 = buildMessage(1L, "param1:100,param2:200,param3:300");
        MessageI m2 = buildMessage(2L, "param1:150,param2:200,param3:300");
        MessageI m3 = buildMessage(2L, "param1:200,param2:200,param3:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(200, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
        assertEquals(250, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m3, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageWithFormula() {
        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}+99", null, Map.of());

        MessageI m1 = buildMessage(1L, "param1:0,param2:200,param3:300");
        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        MessageI m3 = buildMessage(2L, "param1:200,param2:200,param3:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(99, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
        assertEquals(199, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
        assertEquals(299, SensorCalculator.calculateValue(givenSensor, m3, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageWithFormulaAndTwoSensors() {
        SensorI s1 = buildSensor(2L, "test2", new Calibration(), "{@param2}+1", null, Map.of());
        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}+{test2}+99", null, Map.of());

        MessageI m1 = buildMessage(1L, "param1:0,param2:200,param3:300");
        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        MessageI m3 = buildMessage(2L, "param1:200,param2:200,param3:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor, s1.getId(), s1);

        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m1, sensorMap), 0);
        assertEquals(400, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
        assertEquals(500, SensorCalculator.calculateValue(givenSensor, m3, sensorMap), 0);
    }

    @Test
    @Ignore
    public void calculateValueShouldReturnValueFromMessageWithFormulaWhenIllegalSensorName() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "({@param1}+{test2})+99", null, Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");

        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameter() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#speed}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, 0, 0, 0, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(123, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterOdometer() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#gpsOdometer}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, 100.12345678D, 0, 0, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100.12345678D, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterOdometerNaN() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#gpsOdometer}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 0, 0, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterGsmStrength() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#gsmStrength}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 0, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(76, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterBattery() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#battery}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(45, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterOnboardVoltage() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#onboardVoltage}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(12.6, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterOnboardVoltageNaN() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#onboardVoltage}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, Float.NaN, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Float.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterEcoAcceleration() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#ecoAcceleration}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, Float.NaN, 0.1F, 0.2F, 0.3F, 0.4F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(0.1, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterEcoBraking() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#ecoBraking}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, Float.NaN, 0.1F, 0.2F, 0.3F, 0.4F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(0.2, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterEcoCornering() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#ecoCornering}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, Float.NaN, 0.1F, 0.2F, 0.3F, 0.4F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(0.3, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnValueFromMessageParameterEcoBump() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#ecoBump}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, Double.NaN, 76, 45, Float.NaN, 0.1F, 0.2F, 0.3F, 0.4F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(0.4, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnZeroIfUnknownParameter() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{#unknown}", null, Map.of());

        MessageI m2 = new MessageImpl(2L, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 123, 0, 0, 0, 0, 0, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(0, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculateValueShouldReturnNaN() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param2}", "{x}=0", Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_FromParameter() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}", null, Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_FromParameter2() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}+200", null, Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_FromParameterDouble() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}+200", null, Map.of());

        MessageI m2 = buildMessage(2L, "param1:100.356,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(300.356, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_FromParameter_parameter1() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@1}+200", null, Map.of());

        MessageI m2 = buildMessage(2L, "1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(300, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_FromParameter_parameter1_sum() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@1}+{@2}+{@3}", null, Map.of());

        MessageI m2 = buildMessage(2L, "1:100,2:200,3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(600, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_conditional_fromParameter() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}", "{@param1}>500", Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_conditional_fromParameter2() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}", "{@param1}<500", Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(100, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_formula_fromParameter_IfUnexist() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@unexist}", null, Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_conditional_fromParameter_IfUnexist() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}", "{@unexists}>0", Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_Bug() {

        SensorI givenSensor = buildSensor(1L, "power", new Calibration(), "{@66}/1000", "{x}>=0", Map.of());

        MessageI m2 = buildMessage(2L, "param1:100,param2:200,param3:300");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_formula_fromParameter_IfEmpty() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@param1}", null, Map.of());

        MessageI m2 = buildMessage(2L, "");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        assertEquals(Double.NaN, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    @Test
    public void calculate_formula_when_parameter119And9() {

        SensorI givenSensor = buildSensor(1L, "test", new Calibration(), "{@9}", null, Map.of());

        MessageI m2 = buildMessage(2L, "5:0,21:4,240:1,119:1,120:1,141:0,66:13008,67:4334,9:12807,24:0,125:423,_odv:3418469.60");
        Map<Long, SensorI> sensorMap = Map.of(givenSensor.getId(), givenSensor);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            SensorCalculator.calculateValue(givenSensor, m2, sensorMap);
        }
        System.out.println(System.currentTimeMillis() - start);

        assertEquals(12807.0, SensorCalculator.calculateValue(givenSensor, m2, sensorMap), 0);
    }

    private SensorImpl buildSensor(Long id, String name, Calibration calibration, String formulaRaw, String conditionRaw, Map<String, String> options) {
        return new SensorImpl(id, name, 123L, calibration, formulaRaw, conditionRaw, options);
    }

    private MessageI buildMessage(Long id, String params) {
        return new MessageImpl(id, Instant.parse("2020-01-20T10:00:00Z"), 0, 0, 0, 0, 0, 0, 0, 0, 0, 12.6F, 0.1F, 0.1F, 0.1F, 0.1F, params);
    }
}
