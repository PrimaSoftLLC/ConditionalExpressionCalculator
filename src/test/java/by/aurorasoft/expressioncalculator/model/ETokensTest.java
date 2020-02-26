package by.aurorasoft.expressioncalculator.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ETokensTest {

    @Test
    public void createPlus() {
        assertEquals(EToken.operation(EType.PLUS), ETokens.create("+"));
    }

    @Test
    public void createMinus() {
        assertEquals(EToken.operation(EType.MINUS), ETokens.create("-"));
    }

    @Test
    public void createDIVIDE() {
        assertEquals(EToken.operation(EType.DIV), ETokens.create("/"));
    }

    @Test
    public void createMultiply() {
        assertEquals(EToken.operation(EType.MULT), ETokens.create("*"));
    }

    @Test
    public void createMore() {
        assertEquals(EToken.operation(EType.MORE), ETokens.create(">"));
    }

    @Test
    public void createMoreOrEqual() {
        assertEquals(EToken.operation(EType.MORE_E), ETokens.create(">="));
    }

    @Test
    public void createLess() {
        assertEquals(EToken.operation(EType.LESS), ETokens.create("<"));
    }

    @Test
    public void createLessOrEqual() {
        assertEquals(EToken.operation(EType.LESS_E), ETokens.create("<="));
    }

    @Test
    public void createEqual() {
        assertEquals(EToken.operation(EType.EQUAL), ETokens.create("="));
    }

    @Test
    public void createNotEqual() {
        assertEquals(EToken.operation(EType.NOT_E), ETokens.create("!="));
    }

    @Test
    public void createAND() {
        assertEquals(EToken.operation(EType.AND), ETokens.create("&"));
    }

    @Test
    public void createOR() {
        assertEquals(EToken.operation(EType.OR), ETokens.create("|"));
    }

    @Test
    public void createNumb1() {
        assertEquals(EToken.number(122), ETokens.create("122"));
    }

    @Test
    public void createNumb0() {
        assertEquals(EToken.number(0), ETokens.create("0"));
    }

    @Test
    public void createNumb2() {
        assertEquals(EToken.number(123.123), ETokens.create("123.123"));
    }

    @Test
    public void createNumb3() {
        assertEquals(EToken.number(-123), ETokens.create("-123"));
    }

    @Test
    public void createNumb4() {
        assertEquals(EToken.number(-123.123), ETokens.create("-123.123"));
    }

    @Test
    public void createNumbNaN() {
        assertEquals(EToken.number(Double.NaN), ETokens.create("NaN"));
    }

    @Test
    public void createToken() {
        assertEquals(EToken.sensorId(0), ETokens.create("{0}"));
    }

    @Test
    public void createToken2() {
        assertEquals(EToken.sensorId(123), ETokens.create("{123}"));
    }

    @Test
    public void createParam() {
        assertEquals(EToken.messageParamName("speed"), ETokens.create("{#speed}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException1() {
        assertEquals(EToken.sensorId(123), ETokens.create("{x}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException2() {
        assertEquals(EToken.sensorId(123), ETokens.create("{sdf}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException3() {
        assertEquals(EToken.sensorId(123), ETokens.create("s"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException4() {
        assertEquals(EToken.sensorId(123), ETokens.create("{123.45}"));
    }
}
