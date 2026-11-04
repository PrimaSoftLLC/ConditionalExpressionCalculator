package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration;
import by.aurorasoft.expressioncalculator.model.sensor.impl.MessageImpl;
import by.aurorasoft.expressioncalculator.model.sensor.impl.SensorImpl;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class BitSensorTest {
    @Test public void combinesBitwiseFunctionsInSensorFormulaAndCondition() {
        SensorImpl sensor = sensor(1, "flags", "bit_or(bit_and({@flags},255),bit_shl(1,{@index}))",
                "bit_shr({x},3)>0&bit_not({@flags})=0");
        Map<Long, SensorImpl> sensors = Map.of(1L, sensor);
        for (boolean prepared : new boolean[]{false, true}) {
            if (prepared) SensorCalculator.prepare(sensors);
            assertEquals(255, SensorCalculator.calculateValue(sensor, message("flags:-1,index:3"), sensors), 0);
            assertTrue(Double.isNaN(SensorCalculator.calculateValue(sensor, message("flags:8,index:3"), sensors)));
            assertThrows(IllegalArgumentException.class,
                    () -> SensorCalculator.calculateValue(sensor, message("flags:-1,index:54"), sensors));
        }
    }

    @Test public void usesSignedByteTelemetryInFormulaAndCondition() {
        SensorImpl sensor = sensor(1, "flags", "10*bit({@flags},3)", "bit({@flags},8)=1&{x}=10");
        Map<Long, SensorImpl> sensors = Map.of(1L, sensor);
        byte flags = -1;
        for (boolean prepared : new boolean[]{false, true}) {
            if (prepared) SensorCalculator.prepare(sensors);
            assertEquals(10, SensorCalculator.calculateValue(sensor, message("flags:" + flags), sensors), 0);
            assertTrue(Double.isNaN(SensorCalculator.calculateValue(sensor, message("flags:255"), sensors)));
        }
    }

    @Test public void resolvesPropertiesDependenciesAndCurrentValue() {
        SensorImpl source = sensor(1, "source", "{@flags}", null);
        SensorImpl sensor = sensor(2, "result", "bit({source},3)+bit({#speed},3)", "bit({x},1)=1");
        Map<Long, SensorImpl> sensors = Map.of(1L, source, 2L, sensor);
        SensorCalculator.prepare(sensors);
        assertEquals(2, SensorCalculator.calculateValue(sensor, message("flags:8"), sensors), 0);
        assertTrue(Double.isNaN(SensorCalculator.calculateValue(sensor, message("flags:0"), sensors)));
    }

    @Test public void rejectsMissingOrInvalidTelemetryAtCalculationTime() {
        SensorImpl sensor = sensor(1, "flags", "bit({@flags},{@index})", null);
        Map<Long, SensorImpl> sensors = Map.of(1L, sensor);
        SensorCalculator.prepare(sensors);
        for (String params : new String[]{"index:3", "flags:bad,index:3", "flags:1.5,index:3",
                "flags:Infinity,index:3", "flags:9007199254740994,index:3", "flags:8,index:64",
                "flags:8,index:-1", "flags:8,index:0.5", "flags:8"}) {
            assertThrows(params, IllegalArgumentException.class,
                    () -> SensorCalculator.calculateValue(sensor, message(params), sensors));
        }
        assertEquals(1, SensorCalculator.calculateValue(sensor, message("flags:8,index:3"), sensors), 0);
    }

    @Test public void rejectsInvalidConditionArgumentsThroughExternalConditionApi() {
        SensorImpl sensor = sensor(1, "flags", "1", "bit({@flags},0)=1");
        Map<Long, SensorImpl> sensors = Map.of(1L, sensor);
        SensorCalculator.prepare(sensors);
        assertThrows(IllegalArgumentException.class,
                () -> SensorCalculator.calculateValue(sensor, message("flags:1.5"), sensors));
        assertThrows(IllegalArgumentException.class,
                () -> SensorCalculator.applyCondition(sensor, sensor.getCondition(), message("flags:NaN"), sensors));
    }

    @Test public void sharesPreparedTokensAcrossConcurrentMessages() throws Exception {
        SensorImpl sensor = sensor(1, "flags", "bit({@flags},{@index})", "bit({x},0)=1");
        Map<Long, SensorImpl> sensors = Map.of(1L, sensor);
        SensorCalculator.prepare(sensors);
        List<EToken> formula = sensor.getFormula();
        double[] original = formula.stream().mapToDouble(EToken::getValue).toArray();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Callable<Double> signed = () -> SensorCalculator.calculateValue(sensor, message("flags:-1,index:63"), sensors);
            Callable<Double> unsigned = () -> SensorCalculator.calculateValue(sensor, message("flags:255,index:8"), sensors);
            for (int i = 0; i < 50; i++) {
                Future<Double> one = pool.submit(signed);
                Future<Double> zero = pool.submit(unsigned);
                assertEquals(1, one.get(5, TimeUnit.SECONDS), 0);
                assertTrue(Double.isNaN(zero.get(5, TimeUnit.SECONDS)));
            }
        } finally {
            pool.shutdownNow();
        }
        assertSame(formula, sensor.getFormula());
        assertArrayEquals(original, formula.stream().mapToDouble(EToken::getValue).toArray(), 0);
    }

    private static SensorImpl sensor(long id, String name, String formula, String condition) {
        return new SensorImpl(id, name, 1L, new Calibration(), formula, condition, Map.of());
    }

    private static MessageImpl message(String params) {
        return new MessageImpl(1L, Instant.EPOCH, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, params);
    }
}
