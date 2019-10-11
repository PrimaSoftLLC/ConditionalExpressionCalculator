package by.aurorasoft.expressioncalculator.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ETokensTest {

    @Test
    public void createPlus() {
        assertEquals(new EToken(EType.PLUS, -1, -1), ETokens.create("+"));
    }

    @Test
    public void createMinus() {
        assertEquals(new EToken(EType.MINUS, -1, -1), ETokens.create("-"));
    }

    @Test
    public void createDIVIDE() {
        assertEquals(new EToken(EType.DIV, -1, -1), ETokens.create("/"));
    }

    @Test
    public void createMultiply() {
        assertEquals(new EToken(EType.MULT, -1, -1), ETokens.create("*"));
    }

    @Test
    public void createMore() {
        assertEquals(new EToken(EType.MORE, -1, -1), ETokens.create(">"));
    }

    @Test
    public void createMoreOrEqual() {
        assertEquals(new EToken(EType.MORE_E, -1, -1), ETokens.create(">="));
    }

    @Test
    public void createLess() {
        assertEquals(new EToken(EType.LESS, -1, -1), ETokens.create("<"));
    }

    @Test
    public void createLessOrEqual() {
        assertEquals(new EToken(EType.LESS_E, -1, -1), ETokens.create("<="));
    }

    @Test
    public void createEqual() {
        assertEquals(new EToken(EType.EQUAL, -1, -1), ETokens.create("="));
    }

    @Test
    public void createNotEqual() {
        assertEquals(new EToken(EType.NOT_E, -1, -1), ETokens.create("!="));
    }

    @Test
    public void createAND() {
        assertEquals(new EToken(EType.AND, -1, -1), ETokens.create("&"));
    }

    @Test
    public void createOR() {
        assertEquals(new EToken(EType.OR, -1, -1), ETokens.create("|"));
    }

    @Test
    public void createNumb1() {
        assertEquals(new EToken(EType.NUMB, -1, 122), ETokens.create("122"));
    }

    @Test
    public void createNumb0() {
        assertEquals(new EToken(EType.NUMB, -1, 0), ETokens.create("0"));
    }

    @Test
    public void createNumb2() {
        assertEquals(new EToken(EType.NUMB, -1, 123.123f), ETokens.create("123.123"));
    }

    @Test
    public void createNumb3() {
        assertEquals(new EToken(EType.NUMB, -1, -123f), ETokens.create("-123"));
    }

    @Test
    public void createNumb4() {
        assertEquals(new EToken(EType.NUMB, -1, -123.123f), ETokens.create("-123.123"));
    }

    @Test
    public void createNumbNaN() {
        assertEquals(new EToken(EType.NUMB, -1, Float.NaN), ETokens.create("NaN"));
    }

    @Test
    public void createToken() {
        assertEquals(new EToken(EType.TOKEN, 0, -1), ETokens.create("{0}"));
    }

    @Test
    public void createToken2() {
        assertEquals(new EToken(EType.TOKEN, 123, -1), ETokens.create("{123}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException1() {
        assertEquals(new EToken(EType.TOKEN, 123, -1), ETokens.create("{x}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException2() {
        assertEquals(new EToken(EType.TOKEN, 123, -1), ETokens.create("{sdf}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException3() {
        assertEquals(new EToken(EType.TOKEN, 123, -1), ETokens.create("s"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwException4() {
        assertEquals(new EToken(EType.TOKEN, 123, -1), ETokens.create("{123.45}"));
    }
}
