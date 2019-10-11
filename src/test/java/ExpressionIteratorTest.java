import by.aurorasoft.expressioncalculator.ExpressionIterator;
import org.junit.Test;

import static org.junit.Assert.*;
import static testutils.Builder.build;

public class ExpressionIteratorTest {

    @Test
    public void test1() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10>0");
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("0"), expressionIterator.next());
    }

    @Test
    public void test2() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10>=20");
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build(">="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test3() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10<=20");
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build("<="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test4() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10!=20");
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build("!="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test5() {
        ExpressionIterator expressionIterator = new ExpressionIterator("10=20");
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build("="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test6() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}>0");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("0"), expressionIterator.next());
    }

    @Test
    public void test7() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}>=20");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test8() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}<=20");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build("<="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test9() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}!=20");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build("!="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test10() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}=20");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build("="), expressionIterator.next());
        assertEquals(build("20"), expressionIterator.next());
    }

    @Test
    public void test11() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}>{113}");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
    }

    @Test
    public void test12() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}>={113}");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">="), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
    }

    @Test
    public void test13() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}<={113}");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build("<="), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
    }

    @Test
    public void test14() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}!={113}");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build("!="), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
    }

    @Test
    public void test15() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}={113}");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build("="), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
    }

    @Test
    public void test16() {
        ExpressionIterator expressionIterator = new ExpressionIterator("({112}>{113})");
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
    }

    @Test
    public void test17() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}>10&{113}<30");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build("&"), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
        assertEquals(build("<"), expressionIterator.next());
        assertEquals(build("30"), expressionIterator.next());
    }

    @Test
    public void test17_1() {
        ExpressionIterator expressionIterator = new ExpressionIterator("{112}>10|{113}<30");
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build("|"), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
        assertEquals(build("<"), expressionIterator.next());
        assertEquals(build("30"), expressionIterator.next());
    }

    @Test
    public void test18() {
        ExpressionIterator expressionIterator = new ExpressionIterator("({112}>10)&({113}<30)");
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
        assertEquals(build("&"), expressionIterator.next());
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
        assertEquals(build("<"), expressionIterator.next());
        assertEquals(build("30"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
    }

    @Test
    public void test19() {
        ExpressionIterator expressionIterator = new ExpressionIterator("({112}>10)|({113}<30)");
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
        assertEquals(build("|"), expressionIterator.next());
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
        assertEquals(build("<"), expressionIterator.next());
        assertEquals(build("30"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
    }

    @Test
    public void test19spaces() {
        ExpressionIterator expressionIterator = new ExpressionIterator("({112} > 10) | ({113} < 30)");
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{112}"), expressionIterator.next());
        assertEquals(build(">"), expressionIterator.next());
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
        assertEquals(build("|"), expressionIterator.next());
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("{113}"), expressionIterator.next());
        assertEquals(build("<"), expressionIterator.next());
        assertEquals(build("30"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
    }

    @Test
    public void test20() {
        ExpressionIterator expressionIterator = new ExpressionIterator("(6+10-4)/(1+1*2)+1");
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("6"), expressionIterator.next());
        assertEquals(build("+"), expressionIterator.next());
        assertEquals(build("10"), expressionIterator.next());
        assertEquals(build("-"), expressionIterator.next());
        assertEquals(build("4"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
        assertEquals(build("/"), expressionIterator.next());
        assertEquals(build("("), expressionIterator.next());
        assertEquals(build("1"), expressionIterator.next());
        assertEquals(build("+"), expressionIterator.next());
        assertEquals(build("1"), expressionIterator.next());
        assertEquals(build("*"), expressionIterator.next());
        assertEquals(build("2"), expressionIterator.next());
        assertEquals(build(")"), expressionIterator.next());
        assertEquals(build("+"), expressionIterator.next());
        assertEquals(build("1"), expressionIterator.next());
    }
}
