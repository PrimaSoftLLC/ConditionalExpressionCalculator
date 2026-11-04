package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.impl.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SensorCalculatorConcurrencyTest {
    static SensorImpl sensor(long id, String name, String formula, String condition) {
        return new SensorImpl(id, name, 1L, new Calibration(), formula, condition, Map.of());
    }
    static MessageImpl message(double a, double b, Runnable gate) {
        return new MessageImpl(1L, Instant.EPOCH, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, "") {
            @Override public double getParameterValue(String name) {
                if (name.equals("b")) gate.run();
                return name.equals("a") ? a : b;
            }
            @Override public double getPropertyValue(String name) { return a; }
        };
    }
    static void await(CountDownLatch latch) {
        try { assertTrue("gate timed out", latch.await(5, TimeUnit.SECONDS)); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new AssertionError(e); }
    }
    private void overlap(String formula, String condition, boolean external, boolean dependency) throws Exception {
        SensorImpl shared = sensor(1, "shared", formula, condition);
        SensorImpl parent = sensor(2, "parent", "{shared}+1", null);
        Map<Long, SensorImpl> map = Map.of(1L, shared, 2L, parent);
        map.values().forEach(s -> { SensorCalculator.buildFormula(s, map); SensorCalculator.buildCondition(s,map); });
        List<EToken> c = external ? ReversePolishNotation.create("{x}<{@b}", map.values()) : List.of();
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        MessageImpl a = message(1, 2, () -> { entered.countDown(); await(release); });
        MessageImpl b = message(100, 200, () -> {});
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Future<Double> first = pool.submit(() -> external
                ? (SensorCalculator.applyCondition(shared,c,a,map) ? 1d : 0d)
                : SensorCalculator.calculateValue(dependency ? parent : shared,a,map));
            await(entered);
            double second = external ? (SensorCalculator.applyCondition(shared,c,b,map) ? 1 : 0)
                : SensorCalculator.calculateValue(dependency ? parent : shared,b,map);
            release.countDown();
            assertEquals(external ? 1 : dependency ? 4 : condition != null ? 1 : 3, first.get(5,TimeUnit.SECONDS),0);
            assertEquals(external ? 1 : dependency ? 301 : condition != null ? 100 : 300,second,0);
        } finally { release.countDown(); pool.shutdownNow(); }
    }
    @Test public void sharedFormulaKeepsMessageValues() throws Exception { overlap("{@a}+{@b}",null,false,false); }
    @Test public void propertyValuesStayLocal() throws Exception { overlap("{#speed}+{@b}",null,false,false); }
    @Test public void ownConditionKeepsMessageValues() throws Exception { overlap("{@a}","{x}<{@b}",false,false); }
    @Test public void externalConditionKeepsMessageValues() throws Exception { overlap("{@a}",null,true,false); }
    @Test public void sharedDependencyKeepsMessageValues() throws Exception { overlap("{@a}+{@b}",null,false,true); }
    @Test public void tokensAreUnchanged() {
        SensorImpl s = sensor(1,"s","{@a}+{#speed}","{x}<{@b}");
        Map<Long,SensorImpl> map = Map.of(1L,s);
        SensorCalculator.buildFormula(s,map); SensorCalculator.buildCondition(s,map);
        double[] before = s.getFormula().stream().mapToDouble(EToken::getValue).toArray();
        SensorCalculator.calculateValue(s,message(1,20,()->{}),map);
        assertArrayEquals(before,s.getFormula().stream().mapToDouble(EToken::getValue).toArray(),0);
    }
    @Test public void unpreparedCallsDoNotStoreTokens() {
        SensorImpl s = sensor(1,"s","{@a}+{@b}",null);
        assertEquals(3,SensorCalculator.calculateValue(s,message(1,2,()->{}),Map.of(1L,s)),0);
        assertNull(s.getFormula());
    }
}
