import by.aurorasoft.expressioncalculator.ReversePolishNotationStack;
import by.aurorasoft.expressioncalculator.model.EToken;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static testutils.Builder.build;

public class ReversePolishNotationStackTest {

    private final ReversePolishNotationStack reversePolishNotationStack = new ReversePolishNotationStack();

    @Test
    public void test001() {
        List<EToken> actual = reversePolishNotationStack.create("2+2");
        assertEquals(List.of(build("2"), build("2"), build("+")), actual);
    }

    @Test
    public void test() {
        List<EToken> actual = reversePolishNotationStack.create("10>0");
        assertEquals(List.of(build("10"), build("0"), build(">")), actual);
    }

    @Test
    public void test01() {
        List<EToken> actual = reversePolishNotationStack.create("{0}*2+3");
        assertEquals(List.of(build("{0}"), build("2"), build("*"), build("3"), build("+")), actual);
    }

    @Test
    public void test0() {
        List<EToken> actual = reversePolishNotationStack.create("");
        assertEquals(List.of(), actual);
    }

    @Test
    public void test2() {
        List<EToken> actual = reversePolishNotationStack.create("(6+10)");
        assertEquals(List.of(build("6"), build("10"), build("+")), actual);
    }

    @Test
    public void test4() {
        List<EToken> actual = reversePolishNotationStack.create("(6+{123})");
        assertEquals(build(List.of("6", "{123}", "+")), actual);
    }

    @Test
    public void test6() {
        List<EToken> actual = reversePolishNotationStack.create("(10>5)&(5+ 6<4)");
        assertEquals(build(List.of("10", "5", ">", "5", "6", "+", "4", "<", "&")), actual);
    }

    @Test
    public void test5() {
        List<EToken> actual = reversePolishNotationStack.create("(6+10)");
        assertEquals(build(List.of("6", "10", "+")), actual);
    }

    @Test
    public void test7() {
        List<EToken> actual = reversePolishNotationStack.create("(6.567+10)");
        assertEquals(build(List.of("6.567", "10", "+")), actual);
    }
}
