package by.aurorasoft.expressioncalculator.benchmark;

import by.aurorasoft.expressioncalculator.SensorCalculator;
import by.aurorasoft.expressioncalculator.model.sensor.impl.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class SensorCalculationBenchmark {
    @Param({"formula", "condition", "dependency"}) public String scenario;
    private Map<Long,SensorImpl> sensors;
    private SensorImpl target;
    private MessageImpl[] messages;

    private Map<Long,SensorImpl> snapshot() {
        SensorImpl shared = new SensorImpl(1L,"shared",1L,new Calibration(),"{@a}+{@b}*2",null,Map.of());
        SensorImpl parent = new SensorImpl(2L,"parent",1L,new Calibration(),
                scenario.equals("dependency") ? "{shared}+{shared}" : "{@a}+{@b}*2",
                scenario.equals("condition") ? "{x}>0" : null, Map.of());
        Map<Long,SensorImpl> map = Map.of(1L,shared,2L,parent);
        for (SensorImpl s:map.values()) { SensorCalculator.buildFormula(s,map); SensorCalculator.buildCondition(s,map); }
        return map;
    }
    @Setup public void setup() {
        sensors=snapshot(); target=sensors.get(2L); messages=new MessageImpl[256];
        for (int i=0;i<messages.length;i++) messages[i]=new MessageImpl(1L,Instant.EPOCH,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"a:"+(i+1)+",b:2");
        double result=SensorCalculator.calculateValue(target,messages[0],sensors);
        if (result != (scenario.equals("dependency") ? 10 : 5)) throw new AssertionError(result);
    }
    @State(Scope.Thread) public static class Cursor { int value; }
    @Benchmark public double calculate(Cursor cursor) {
        return SensorCalculator.calculateValue(target,messages[cursor.value++ & 255],sensors);
    }
    @Benchmark public Object prepareSnapshot() { return snapshot(); }
}
