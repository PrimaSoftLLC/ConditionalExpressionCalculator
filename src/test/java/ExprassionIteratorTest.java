import org.junit.Test;

import static org.junit.Assert.*;

public class ExprassionIteratorTest {

    @Test
    public void test1() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("10>0");
        assertEquals("10", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("0", exprassionIterator.next());
    }

    @Test
    public void test2() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("10>=20");
        assertEquals("10", exprassionIterator.next());
        assertEquals(">=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test3() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("10<=20");
        assertEquals("10", exprassionIterator.next());
        assertEquals("<=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test4() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("10!=20");
        assertEquals("10", exprassionIterator.next());
        assertEquals("!=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test5() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("10=20");
        assertEquals("10", exprassionIterator.next());
        assertEquals("=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test6() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}>0");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("0", exprassionIterator.next());
    }

    @Test
    public void test7() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}>=20");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test8() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}<=20");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals("<=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test9() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}!=20");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals("!=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test10() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}=20");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals("=", exprassionIterator.next());
        assertEquals("20", exprassionIterator.next());
    }

    @Test
    public void test11() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}>{113}");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
    }

    @Test
    public void test12() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}>={113}");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">=", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
    }

    @Test
    public void test13() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}<={113}");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals("<=", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
    }

    @Test
    public void test14() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}!={113}");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals("!=", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
    }

    @Test
    public void test15() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}={113}");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals("=", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
    }

    @Test
    public void test16() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("({112}>{113})");
        assertEquals("(", exprassionIterator.next());
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
    }

    @Test
    public void test17() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}>10&{113}<30");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("10", exprassionIterator.next());
        assertEquals("&", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
        assertEquals("<", exprassionIterator.next());
        assertEquals("30", exprassionIterator.next());
    }

    @Test
    public void test17_1() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("{112}>10|{113}<30");
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("10", exprassionIterator.next());
        assertEquals("|", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
        assertEquals("<", exprassionIterator.next());
        assertEquals("30", exprassionIterator.next());
    }

    @Test
    public void test18() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("({112}>10)&({113}<30)");
        assertEquals("(", exprassionIterator.next());
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("10", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
        assertEquals("&", exprassionIterator.next());
        assertEquals("(", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
        assertEquals("<", exprassionIterator.next());
        assertEquals("30", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
    }

    @Test
    public void test19() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("({112}>10)|({113}<30)");
        assertEquals("(", exprassionIterator.next());
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("10", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
        assertEquals("|", exprassionIterator.next());
        assertEquals("(", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
        assertEquals("<", exprassionIterator.next());
        assertEquals("30", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
    }

    @Test
    public void test19spaces() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("({112} > 10) | ({113} < 30)");
        assertEquals("(", exprassionIterator.next());
        assertEquals("{112}", exprassionIterator.next());
        assertEquals(">", exprassionIterator.next());
        assertEquals("10", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
        assertEquals("|", exprassionIterator.next());
        assertEquals("(", exprassionIterator.next());
        assertEquals("{113}", exprassionIterator.next());
        assertEquals("<", exprassionIterator.next());
        assertEquals("30", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
    }

    @Test
    public void test21() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("(2+2)/(4*2)");
        while(exprassionIterator.hasNext()){
            System.out.println(exprassionIterator.next());
        }
    }

    @Test
    public void test20() {
        ExprassionIterator exprassionIterator = new ExprassionIterator("(6+10-4)/(1+1*2)+1");
        assertEquals("(", exprassionIterator.next());
        assertEquals("6", exprassionIterator.next());
        assertEquals("+", exprassionIterator.next());
        assertEquals("10", exprassionIterator.next());
        assertEquals("-", exprassionIterator.next());
        assertEquals("4", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
        assertEquals("/", exprassionIterator.next());
        assertEquals("(", exprassionIterator.next());
        assertEquals("1", exprassionIterator.next());
        assertEquals("+", exprassionIterator.next());
        assertEquals("1", exprassionIterator.next());
        assertEquals("*", exprassionIterator.next());
        assertEquals("2", exprassionIterator.next());
        assertEquals(")", exprassionIterator.next());
        assertEquals("+", exprassionIterator.next());
        assertEquals("1", exprassionIterator.next());
    }
}
