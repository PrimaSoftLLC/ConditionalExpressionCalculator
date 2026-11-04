package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.impl.SensorImpl;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SensorPreparationTest {
    @Test public void unpreparedConcurrentCallsNeverInvokeSetters() throws Exception {
        SensorImpl s = new SensorImpl(1L,"s",1L,new by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration(),"{@a}+{@b}","{x}>0",Map.of()) {
            @Override public void setFormula(List<EToken> tokens) { throw new AssertionError("formula write during calculation"); }
            @Override public void setCondition(List<EToken> tokens) { throw new AssertionError("condition write during calculation"); }
        };
        Map<Long,SensorImpl> map=Map.of(1L,s);
        java.util.concurrent.ExecutorService pool=java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            java.util.concurrent.Future<Double> a=pool.submit(()->SensorCalculator.calculateValue(s,SensorCalculatorConcurrencyTest.message(1,2,()->{}),map));
            java.util.concurrent.Future<Double> b=pool.submit(()->SensorCalculator.calculateValue(s,SensorCalculatorConcurrencyTest.message(100,200,()->{}),map));
            assertEquals(3,a.get(5,java.util.concurrent.TimeUnit.SECONDS),0);
            assertEquals(300,b.get(5,java.util.concurrent.TimeUnit.SECONDS),0);
            assertNull(s.getFormula()); assertNull(s.getCondition());
        } finally { pool.shutdownNow(); }
    }
    @Test public void preparedCallsNeverResolveNamesAgain() {
        java.util.concurrent.atomic.AtomicBoolean prepared=new java.util.concurrent.atomic.AtomicBoolean();
        SensorImpl leaf=new SensorImpl(1L,"leaf",1L,new by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration(),"{@a}",null,Map.of()) {
            @Override public String getName() { if(prepared.get()) throw new AssertionError("recompiled"); return super.getName(); }
            @Override public void setFormula(List<EToken> tokens) { if(prepared.get()) throw new AssertionError("rewritten"); super.setFormula(tokens); }
        };
        SensorImpl parent=SensorCalculatorConcurrencyTest.sensor(2,"parent","{leaf}+1","{leaf}>0");
        Map<Long,SensorImpl> map=Map.of(1L,leaf,2L,parent);
        SensorCalculator.prepare(map); prepared.set(true);
        for(int i=0;i<3;i++) assertEquals(2,SensorCalculator.calculateValue(parent,SensorCalculatorConcurrencyTest.message(1,2,()->{}),map),0);
    }
    @Test public void currentValueUsesCalibrationAndLegacyValuesRemainSupported() {
        SensorImpl s = new SensorImpl(1L,"s",1L,
            new by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration(List.of(
                new by.aurorasoft.expressioncalculator.model.sensor.impl.RawData(0,0,""),
                new by.aurorasoft.expressioncalculator.model.sensor.impl.RawData(10,20,""))),
            "{@a}","{x}=20",Map.of());
        Map<Long,SensorImpl> map=Map.of(1L,s);
        SensorCalculator.prepare(map);
        assertEquals(20,SensorCalculator.calculateValue(s,SensorCalculatorConcurrencyTest.message(10,2,()->{}),map),0);
        EToken legacy=EToken.parameter("a"); legacy.setValue(9);
        assertEquals(9,ExpressionCalculator.calculateMath(List.of(legacy)),0);
        SensorImpl self=SensorCalculatorConcurrencyTest.sensor(1,"s","{x}+1",null);
        assertEquals(1,SensorCalculator.calculateValue(self,SensorCalculatorConcurrencyTest.message(10,2,()->{}),Map.of(1L,self)),0);
    }
    @Test public void nestedDependenciesRetainIndependentStacksAndCallCounts() {
        java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
        SensorImpl leaf=new SensorImpl(1L,"leaf",1L,new by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration(),null,null,Map.of()) {
            @Override public double calculateValue(by.aurorasoft.expressioncalculator.model.sensor.MessageI m,
                Map<Long,? extends by.aurorasoft.expressioncalculator.model.sensor.SensorI> map) { calls.incrementAndGet(); return 3; }
        };
        SensorImpl middle=SensorCalculatorConcurrencyTest.sensor(2,"middle","{leaf}+2",null);
        SensorImpl parent=SensorCalculatorConcurrencyTest.sensor(3,"parent","{middle}*4+{leaf}",null);
        Map<Long,SensorImpl> map=Map.of(1L,leaf,2L,middle,3L,parent);
        SensorCalculator.prepare(map);
        assertEquals(23,SensorCalculator.calculateValue(parent,SensorCalculatorConcurrencyTest.message(1,2,()->{}),map),0);
        assertEquals(2,calls.get());
    }
    @Test public void logicalOperandsAreResolvedWithoutShortCircuit() {
        SensorImpl s=SensorCalculatorConcurrencyTest.sensor(1,"s","1","{@a}>0|{@b}>0");
        Map<Long,SensorImpl> map=Map.of(1L,s);
        SensorCalculator.prepare(map);
        java.util.concurrent.atomic.AtomicInteger reads=new java.util.concurrent.atomic.AtomicInteger();
        assertEquals(1,SensorCalculator.calculateValue(s,SensorCalculatorConcurrencyTest.message(1,2,reads::incrementAndGet),map),0);
        assertEquals(1,reads.get());
    }
    @Test public void prepareCompilesAndReusesFullSnapshot() {
        SensorImpl shared = SensorCalculatorConcurrencyTest.sensor(1,"shared","{@a}+{@b}",null);
        SensorImpl parent = SensorCalculatorConcurrencyTest.sensor(2,"parent","{shared}+1","{x}>0");
        Map<Long,SensorImpl> map = Map.of(1L,shared,2L,parent);
        SensorCalculator.prepare(map);
        List<EToken> formula = parent.getFormula(), condition = parent.getCondition();
        assertNotNull(shared.getFormula()); assertNotNull(condition);
        assertEquals(4,SensorCalculator.calculateValue(parent,SensorCalculatorConcurrencyTest.message(1,2,()->{}),map),0);
        SensorCalculator.prepare(map);
        assertSame(formula,parent.getFormula()); assertSame(condition,parent.getCondition());
        assertThrows(UnsupportedOperationException.class, () -> formula.add(EToken.number(9)));
    }
    @Test public void invalidAndEmptyExpressionsKeepFallback() {
        for (String raw : Arrays.asList(null,"","{unknown}")) {
            SensorImpl s = SensorCalculatorConcurrencyTest.sensor(1,"s",raw,raw);
            Map<Long,SensorImpl> map = Map.of(1L,s);
            SensorCalculator.prepare(map);
            assertEquals(0,SensorCalculator.calculateValue(s,SensorCalculatorConcurrencyTest.message(1,2,()->{}),map),0);
        }
        SensorCalculator.prepare(Map.of());
    }
    @Test public void existingListsAndCustomDependencyArePreserved() {
        SensorImpl shared = new SensorImpl(1L,"shared",1L,new by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration(),"1",null,Map.of()) {
            @Override public double calculateValue(by.aurorasoft.expressioncalculator.model.sensor.MessageI m,
                    Map<Long,? extends by.aurorasoft.expressioncalculator.model.sensor.SensorI> map) { return 42; }
        };
        SensorImpl parent = SensorCalculatorConcurrencyTest.sensor(2,"parent","{shared}+{shared}",null);
        Map<Long,SensorImpl> map = Map.of(1L,shared,2L,parent);
        List<EToken> original = ReversePolishNotation.create(parent.getFormulaRaw(),map.values());
        parent.setFormula(original);
        SensorCalculator.prepare(map);
        assertSame(original,parent.getFormula());
        assertEquals(84,SensorCalculator.calculateValue(parent,SensorCalculatorConcurrencyTest.message(1,2,()->{}),map),0);
    }
}
