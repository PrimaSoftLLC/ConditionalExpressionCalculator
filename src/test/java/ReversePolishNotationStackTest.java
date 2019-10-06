import by.aurorasoft.expressioncalculator.ReversePolishNotationStack;
import org.junit.Test;

import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReversePolishNotationStackTest {

    private final ReversePolishNotationStack reversePolishNotationStack = new ReversePolishNotationStack();

    @Test
    public void test() {
        Deque actual = reversePolishNotationStack.create("10>0");
        assertEquals(List.of("10", "0", ">"), actual);
    }

    @Test
    public void test01() {
        Deque actual = reversePolishNotationStack.create("{x}*2+3");
        assertEquals(List.of("{x}", "2", "*", "3", "+"), actual);
    }

    @Test
    public void test0() {
        Deque actual = reversePolishNotationStack.create("");
        assertEquals(List.of(), actual);
    }

    @Test
    public void test2() {
        Deque actual = reversePolishNotationStack.create("(6+10)");
        assertEquals(List.of("6", "10", "+"), actual);
    }

    @Test
    public void test4() {
        Deque actual = reversePolishNotationStack.create("(6+{123})");
        assertEquals(List.of("6", "{123}", "+"), actual);
    }

    @Test
    public void test6() {
        Deque actual = reversePolishNotationStack.create("(10>5)&(5+ 6<4)");
        assertEquals(List.of("10", "5", ">", "5", "6", "+", "4", "<", "&"), actual);
    }

    @Test
    public void test5() {
        Deque actual = reversePolishNotationStack.create("(6+10)");
        assertEquals(List.of("6", "10", "+"), actual);
    }
}
