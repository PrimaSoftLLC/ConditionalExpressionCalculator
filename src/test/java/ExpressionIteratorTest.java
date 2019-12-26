import by.aurorasoft.expressioncalculator.ExpressionIterator;
import by.aurorasoft.expressioncalculator.model.RawToken;
import by.aurorasoft.expressioncalculator.model.RawTokenImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static testutils.Builder.build;

public class ExpressionIteratorTest {

    @Test
    public void test1() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10>0", List.of());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("0"), expressionIterator.nextEToken());
    }

    @Test
    public void test2() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10>=20", List.of());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build(">="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test3() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10<=20", List.of());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build("<="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test4() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10!=20", List.of());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build("!="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test5() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10=20", List.of());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build("="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test6() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}>0", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("0"), expressionIterator.nextEToken());
    }

    @Test
    public void test7() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}>=20", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test8() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}<=20", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build("<="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test9() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}!=20", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build("!="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test10() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}=20", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build("="), expressionIterator.nextEToken());
        assertEquals(build("20"), expressionIterator.nextEToken());
    }

    @Test
    public void test11() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}>{sensor2}", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
    }

    @Test
    public void test12() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}>={sensor2}",rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">="), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
    }

    @Test
    public void test13() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}<={sensor2}", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build("<="), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
    }

    @Test
    public void test14() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}!={sensor2}", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build("!="), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
    }

    @Test
    public void test15() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}={sensor2}", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build("="), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
    }

    @Test
    public void test16() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("({sensor1}>{sensor2})", rawTokens);
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
    }

    @Test
    public void test17() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}>10&{sensor2}<30", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build("&"), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
        assertEquals(build("<"), expressionIterator.nextEToken());
        assertEquals(build("30"), expressionIterator.nextEToken());
    }

    @Test
    public void test17_1() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("{sensor1}>10|{sensor2}<30", rawTokens);
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build("|"), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
        assertEquals(build("<"), expressionIterator.nextEToken());
        assertEquals(build("30"), expressionIterator.nextEToken());
    }

    @Test
    public void test18() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("({sensor1}>10)&({sensor2}<30)", rawTokens);
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
        assertEquals(build("&"), expressionIterator.nextEToken());
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
        assertEquals(build("<"), expressionIterator.nextEToken());
        assertEquals(build("30"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
    }

    @Test
    public void test19() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("({sensor1}>10)|({sensor2}<30)", rawTokens);
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
        assertEquals(build("|"), expressionIterator.nextEToken());
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
        assertEquals(build("<"), expressionIterator.nextEToken());
        assertEquals(build("30"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
    }

    @Test
    public void test19spaces() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("({sensor1} > 10) | ({sensor2} < 30)", rawTokens);
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{112}"), expressionIterator.nextEToken());
        assertEquals(build(">"), expressionIterator.nextEToken());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
        assertEquals(build("|"), expressionIterator.nextEToken());
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("{113}"), expressionIterator.nextEToken());
        assertEquals(build("<"), expressionIterator.nextEToken());
        assertEquals(build("30"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
    }

    @Test
    public void test20() {
        List<RawToken> rawTokens = List.of(new RawTokenImpl(112l, "sensor1"), new RawTokenImpl(113l, "sensor2"));
        ExpressionIterator expressionIterator = new ExpressionIterator("(6+10-4)/(1+1*2)+1", rawTokens);
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("6"), expressionIterator.nextEToken());
        assertEquals(build("+"), expressionIterator.nextEToken());
        assertEquals(build("10"), expressionIterator.nextEToken());
        assertEquals(build("-"), expressionIterator.nextEToken());
        assertEquals(build("4"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
        assertEquals(build("/"), expressionIterator.nextEToken());
        assertEquals(build("("), expressionIterator.nextEToken());
        assertEquals(build("1"), expressionIterator.nextEToken());
        assertEquals(build("+"), expressionIterator.nextEToken());
        assertEquals(build("1"), expressionIterator.nextEToken());
        assertEquals(build("*"), expressionIterator.nextEToken());
        assertEquals(build("2"), expressionIterator.nextEToken());
        assertEquals(build(")"), expressionIterator.nextEToken());
        assertEquals(build("+"), expressionIterator.nextEToken());
        assertEquals(build("1"), expressionIterator.nextEToken());
    }
}
