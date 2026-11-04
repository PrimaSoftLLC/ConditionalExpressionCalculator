package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.impl.SensorImpl;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SensorPreparationTest {
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
