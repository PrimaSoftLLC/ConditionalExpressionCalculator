import by.aurorasoft.expressioncalculator.ExpressionCalculator;
import by.aurorasoft.expressioncalculator.ExpressionChecker;
import by.aurorasoft.expressioncalculator.ReversePolishNotation;
import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static testutils.Builder.build;

public class BitwiseFunctionsTest {
    private final ExpressionChecker checker = new ExpressionChecker();
    private static final String[] BINARY = {"bit_and", "bit_or", "bit_xor", "bit_shl", "bit_shr", "bit_ushr"};
    private static final String[] SHIFTS = {"bit_shl", "bit_shr", "bit_ushr"};

    @Test public void combinesBitsAndSignExtendsNegativeBytes() {
        assertEquals(8, math("bit_and(12,10)"), 0);
        assertEquals(14, math("bit_or(12,10)"), 0);
        assertEquals(6, math("bit_xor(12,10)"), 0);
        byte flags = -1;
        assertEquals(255, resolve("bit_and({@flags},255)", Map.of("flags", (double) flags)), 0);
        assertEquals(-125, math("bit_or((0-128),3)"), 0);
        assertEquals(-256, math("bit_xor((0-1),255)"), 0);
        assertEquals(9007199254740992d, math("bit_or(9007199254740992,0)"), 0);
        assertEquals(-9007199254740992d, math("bit_and((0-9007199254740992),(0-1))"), 0);
    }

    @Test public void unaryNotConsumesExactlyOneValue() {
        assertEquals(-1, math("bit_not(0)"), 0);
        assertEquals(0, math("bit_not((0-1))"), 0);
        assertEquals(-256, math("bit_not(255)"), 0);
        assertEquals(8, math("10+bit_not(0)*2"), 0);
        assertEquals(255, math("bit_not(bit_not(255))"), 0);
        assertEquals(9007199254740991d, math("bit_not((0-9007199254740992))"), 0);
        assertEquals(-9007199254740992d, math("bit_not(9007199254740991)"), 0);
        assertTrue(ExpressionCalculator.calculateConditional(parse("1=1&bit_not(0)=(0-1)")));
        assertTrue(ExpressionCalculator.calculateConditional(parse("1=bit_not((0-2))")));
        assertEquals("bit_not({@flags})", checker.checkMath(" bit_not( {@flags} ) "));
    }

    @Test public void shiftsUsingAll64LongBits() {
        assertEquals(8, math("bit_shl(1,3)"), 0);
        assertEquals(4294967296d, math("bit_shl(1,32)"), 0);
        assertEquals(9007199254740992d, math("bit_shl(1,53)"), 0);
        assertEquals(-9007199254740992d, math("bit_shl((0-1),53)"), 0);
        assertEquals(-1, math("bit_shr((0-128),7)"), 0);
        assertEquals(0, math("bit_shr(255,8)"), 0);
        assertEquals(-1, math("bit_shr((0-1),63)"), 0);
        assertEquals(1, math("bit_ushr((0-1),63)"), 0);
        assertEquals(9007199254740991d, math("bit_ushr((0-1),11)"), 0);
        assertEquals(0, math("bit_ushr(255,63)"), 0);
        // Java long shifts discard bits leaving the 64-bit representation.
        assertEquals(0, math("bit_shl(2,63)"), 0);
        for (String function : SHIFTS) {
            assertEquals(-1, math(function + "((0-1),0)"), 0);
        }
    }

    @Test public void emitsRpnForMixedNestedFunctionsAndArithmetic() {
        String expression = "2*bit_or(bit_and(15,3),bit_shl(1,3))+bit_not(0)";
        assertEquals(build(List.of("2", "15", "3", "bit_and", "1", "3", "bit_shl",
                "bit_or", "*", "0", "bit_not", "+")), parse(expression));
        assertEquals(21, math(expression), 0);
        assertEquals(1, math("bit(bit_or(0,8),3)"), 0);
        String condition = "bit_and(12,8)=8&bit_xor(3,1)=2|bit_not(0)=0";
        assertEquals(condition, checker.checkLogic(condition));
        assertTrue(ExpressionCalculator.calculateConditional(parse(condition)));
    }

    @Test public void rejectsResultsOutsideRangeBeforeRoundingToDouble() {
        for (String expression : new String[]{"bit_not(9007199254740992)",
                "bit_or(9007199254740992,1)", "bit_xor(9007199254740992,1)",
                "bit_xor((0-1),9007199254740992)", "bit_shl(1,54)", "bit_shl(1,63)",
                "bit_ushr((0-1),1)", "bit_ushr((0-1),10)",
                "bit_and(bit_shl(1,54),0)"}) {
            List<EToken> tokens = parse(expression);
            assertThrows(expression, IllegalArgumentException.class, () -> ExpressionCalculator.calculateMath(tokens));
            assertThrows(expression, IllegalArgumentException.class, () -> checker.checkMath(expression));
            assertThrows(expression, IllegalArgumentException.class, () -> checker.checkLogic(expression + "=0"));
        }
    }

    @Test public void rejectsInvalidNumbersInEveryFunction() {
        double[] invalid = {1.5, -1.5, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Math.nextUp(9007199254740992d), Math.nextDown(-9007199254740992d)};
        for (double value : invalid) {
            for (String function : BINARY) {
                List<EToken> tokens = parse(function + "({@a},0)");
                assertThrows(function, IllegalArgumentException.class, () -> calculate(tokens, Map.of("a", value)));
            }
            List<EToken> unary = parse("bit_not({@a})");
            assertThrows(IllegalArgumentException.class, () -> calculate(unary, Map.of("a", value)));
            for (String function : new String[]{"bit_and", "bit_or", "bit_xor"}) {
                List<EToken> tokens = parse(function + "(0,{@b})");
                assertThrows(function, IllegalArgumentException.class, () -> calculate(tokens, Map.of("b", value)));
            }
        }
    }

    @Test public void rejectsInvalidShiftCountsInsteadOfMaskingThem() {
        for (String function : SHIFTS) {
            List<EToken> tokens = parse(function + "(1,{@n})");
            for (double count : new double[]{-1, 64, 65, 1.5, Double.NaN, Double.POSITIVE_INFINITY}) {
                assertThrows(function, IllegalArgumentException.class, () -> calculate(tokens, Map.of("n", count)));
            }
        }
    }

    @Test public void validatesKnownArgumentsEvenWhenOtherArgumentsAreDynamic() {
        for (String function : BINARY) {
            assertThrows(function, IllegalArgumentException.class, () -> checker.checkMath(function + "(1.5,{@b})"));
            assertThrows(function, IllegalArgumentException.class, () -> checker.checkMath(function + "({@a},1.5)"));
            String dynamic = function + "({@a},{@b})";
            assertEquals(dynamic, checker.checkMath(dynamic));
            assertEquals(dynamic + "=1", checker.checkLogic(dynamic + "=1"));
        }
        for (String function : SHIFTS) {
            assertThrows(function, IllegalArgumentException.class, () -> checker.checkMath(function + "({@a},64)"));
            String dynamic = function + "({@a},1/({@n}+1))";
            assertEquals(dynamic, checker.checkMath(dynamic));
        }
        assertThrows(IllegalArgumentException.class, () -> checker.checkMath("bit_not(1/0)"));
        assertThrows(IllegalArgumentException.class, () -> checker.checkMath("bit_or({@a},bit_not(9007199254740992))"));
    }

    @Test public void rejectsWrongArityAndBooleanArguments() {
        for (String function : BINARY) {
            for (String args : new String[]{"", "1", "1,", ",1", "1,2,3", "1=1,0", "1,0=0"}) {
                assertInvalid(function + "(" + args + ")");
            }
        }
        for (String expression : new String[]{"bit_not", "bit_not()", "bit_not(1,2)",
                "bit_not(1,)", "bit_not(1=1)", "bit_not(1", "bit_not(1))", "bit_not(1)2",
                "bit_or(1,(2,3))", "bit_and(1,1)&bit_not(0)", "bit_not(0)+(1=1)"}) {
            assertInvalid(expression);
        }
        assertThrows(IllegalArgumentException.class, () -> checker.checkLogic("bit_not(0)"));
        assertThrows(IllegalArgumentException.class, () -> checker.checkMath("bit_not(0)=0"));
    }

    @Test public void preservesEagerLogicalEvaluation() {
        List<EToken> tokens = parse("1=1|bit_not({@a})=0");
        assertThrows(IllegalArgumentException.class, () -> ExpressionCalculator.calculateConditional(tokens,
                t -> t.is(EType.PARAMETER) ? Double.NaN : t.getValue()));
    }

    private void assertInvalid(String expression) {
        assertThrows(expression, IllegalArgumentException.class, () -> parse(expression));
        assertThrows(expression, IllegalArgumentException.class, () -> checker.checkMath(expression));
    }

    private static List<EToken> parse(String expression) {
        return ReversePolishNotation.create(expression, List.of());
    }

    private static double math(String expression) {
        return ExpressionCalculator.calculateMath(parse(expression));
    }

    private static double resolve(String expression, Map<String, Double> values) {
        return calculate(parse(expression), values);
    }

    private static double calculate(List<EToken> tokens, Map<String, Double> values) {
        return ExpressionCalculator.calculateMath(tokens,
                t -> t.is(EType.PARAMETER) ? values.get(t.getParameterName()) : t.getValue());
    }
}
