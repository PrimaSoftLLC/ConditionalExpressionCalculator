package by.aurorasoft.expressioncalculator.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ETokensTest {

    @Test
    public void createPlus() {
        assertEquals(new EToken(EType.PLUS), ETokens.create("+"));
    }

    @Test
    public void createMinus() {
        assertEquals(new EToken(EType.MINUS), ETokens.create("-"));
    }

    @Test
    public void createDIVIDE() {
        assertEquals(new EToken(EType.DIV), ETokens.create("/"));
    }

    @Test
    public void createMultiply() {
        assertEquals(new EToken(EType.MULT), ETokens.create("*"));
    }

    @Test
    public void createMore() {
        assertEquals(new EToken(EType.MORE), ETokens.create(">"));
    }

    @Test
    public void createMoreOrEqual() {
        assertEquals(new EToken(EType.MORE_E), ETokens.create(">="));
    }

    @Test
    public void createLess() {
        assertEquals(new EToken(EType.LESS), ETokens.create("<"));
    }

    @Test
    public void createLessOrEqual() {
        assertEquals(new EToken(EType.LESS_E), ETokens.create("<="));
    }

    @Test
    public void createEqual() {
        assertEquals(new EToken(EType.EQUAL), ETokens.create("="));
    }

    @Test
    public void createNotEqual() {
        assertEquals(new EToken(EType.NOT_E), ETokens.create("!="));
    }

    @Test
    public void createAND() {
        assertEquals(new EToken(EType.AND), ETokens.create("&"));
    }

    @Test
    public void createOR() {
        assertEquals(new EToken(EType.OR), ETokens.create("|"));
    }

    @Test
    public void createNumb1() {
        assertEquals(new EToken(EType.NUMB, 122f), ETokens.create("122"));
    }

    @Test
    public void createNumb0() {
        assertEquals(new EToken(EType.NUMB, 0f), ETokens.create("0"));
    }

    @Test
    public void createNumb2() {
        assertEquals(new EToken(EType.NUMB, 123.123f), ETokens.create("123.123"));
    }

    @Test
    public void createNumb3() {
        assertEquals(new EToken(EType.NUMB, -123f), ETokens.create("-123"));
    }

    @Test
    public void createNumb4() {
        assertEquals(new EToken(EType.NUMB, -123.123f), ETokens.create("-123.123"));
    }

    @Test
    public void createNumbNaN() {
        assertEquals(new EToken(EType.NUMB, Float.NaN), ETokens.create("NaN"));
    }

    @Test
    public void createToken() {
        assertEquals(new EToken(EType.TOKEN, 0), ETokens.create("{0}"));
    }

    @Test
    public void createToken2() {
        assertEquals(new EToken(EType.TOKEN, 123), ETokens.create("{123}"));
    }

    @Test
    public void createParam() {
        assertEquals(new EToken(EType.PARAM, "speed"), ETokens.create("{#speed}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException1() {
        assertEquals(new EToken(EType.TOKEN, 123), ETokens.create("{x}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException2() {
        assertEquals(new EToken(EType.TOKEN, 123), ETokens.create("{sdf}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException3() {
        assertEquals(new EToken(EType.TOKEN, 123), ETokens.create("s"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException4() {
        assertEquals(new EToken(EType.TOKEN, 123), ETokens.create("{123.45}"));
    }
}
