import by.aurorasoft.expressioncalculator.ReversePolishNotation;
import by.aurorasoft.expressioncalculator.model.EToken;

import by.aurorasoft.expressioncalculator.model.sensor.SensorI;
import by.aurorasoft.expressioncalculator.model.sensor.impl.SensorImpl;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static testutils.Builder.build;

public class ReversePolishNotationTest {

    @Test
    public void test001() {
        List<EToken> actual = ReversePolishNotation.create("2+2", List.of());
        assertEquals(List.of(build("2"), build("2"), build("+")), actual);
    }

    @Test
    public void test() {
        List<EToken> actual = ReversePolishNotation.create("10>0", List.of());
        assertEquals(List.of(build("10"), build("0"), build(">")), actual);
    }

    @Test
    public void test01() {
        List<SensorI> rawTokens = List.of(new SensorImpl(123L, "speed-sensor", 123L,  null, null, null, Map.of()));
        List<EToken> actual = ReversePolishNotation.create("{x}*2+3", rawTokens);
        assertEquals(List.of(build("{0}"), build("2"), build("*"), build("3"), build("+")), actual);
    }

    @Test
    public void test0() {
        List<EToken> actual = ReversePolishNotation.create("", List.of());
        assertEquals(List.of(), actual);
    }

    @Test
    public void test2() {
        List<EToken> actual = ReversePolishNotation.create("(6+10)", List.of());
        assertEquals(List.of(build("6"), build("10"), build("+")), actual);
    }

    @Test
    public void test4() {
        List<SensorI> rawTokens = List.of(new SensorImpl(123L, "rpm sensor", 123L, null, null, null, Map.of()));
        List<EToken> actual = ReversePolishNotation.create("(6+{rpm sensor})", rawTokens);
        assertEquals(build(List.of("6", "{123}", "+")), actual);
    }

    @Test
    public void testProperty() {
        List<SensorI> rawTokens = List.of();
        List<EToken> actual = ReversePolishNotation.create("(6+{#speed})", rawTokens);
        assertEquals(build(List.of("6", "{#speed}", "+")), actual);
    }

    @Test
    public void testParameter() {
        List<SensorI> rawTokens = List.of();
        List<EToken> actual = ReversePolishNotation.create("(6+{@VBAT})", rawTokens);
        assertEquals(build(List.of("6", "{@VBAT}", "+")), actual);
    }

    @Test
    public void test6() {

        List<EToken> actual = ReversePolishNotation.create("(10>5)&(5+6<4)", List.of());
        assertEquals(build(List.of("10", "5", ">", "5", "6", "+", "4", "<", "&")), actual);
    }

    @Test
    public void test66() {

        List<EToken> actual = ReversePolishNotation.create("10<5", List.of());
        assertEquals(build(List.of("10", "5","<")), actual);
    }

    @Test
    public void test5() {
        List<EToken> actual = ReversePolishNotation.create("(6+10)", List.of());
        assertEquals(build(List.of("6", "10", "+")), actual);
    }

    @Test
    public void test7() {
        List<EToken> actual = ReversePolishNotation.create("(6.567+10)", List.of());
        assertEquals(build(List.of("6.567", "10", "+")), actual);
    }
}
