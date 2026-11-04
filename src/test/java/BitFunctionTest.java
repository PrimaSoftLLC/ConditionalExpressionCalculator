import by.aurorasoft.expressioncalculator.ExpressionCalculator;
import by.aurorasoft.expressioncalculator.ExpressionChecker;
import by.aurorasoft.expressioncalculator.ReversePolishNotation;
import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BitFunctionTest {
    private final ExpressionChecker checker = new ExpressionChecker();

    @Test public void extractsBitsAcrossLongWidth() {
        String[] expressions = {"bit(0,0)", "bit(1,0)", "bit(8,3)", "bit(8,2)",
                "bit(2147483648,31)", "bit(4294967296,32)", "bit(4503599627370496,52)",
                "bit(9007199254740992,53)", "bit(9007199254740992,63)",
                "bit((0-9007199254740992),53)", "bit((0-9007199254740992),63)"};
        double[] expected = {0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1};
        for (int i = 0; i < expressions.length; i++) {
            assertEquals(expressions[i], expected[i], math(expressions[i]), 0);
            assertEquals(expressions[i], checker.checkMath(expressions[i]));
        }
    }

    @Test public void signExtendsNegativeByteWithoutGuessingSourceWidth() {
        byte flags = -1;
        List<EToken> expression = parse("bit({@flags},{@index})");
        for (int index : new int[]{0, 7, 8, 31, 32, 63}) {
            final int position = index;
            assertEquals(1, ExpressionCalculator.calculateMath(expression,
                    t -> "flags".equals(t.getParameterName()) ? flags : position), 0);
        }
        assertEquals(0, math("bit(255,8)"), 0);
        assertEquals(1, math("bit((0-128),7)"), 0);
        assertEquals(0, math("bit((0-128),6)"), 0);
        assertEquals(1, math("bit((0-128),63)"), 0);
    }

    @Test public void acceptsNumericExpressionsAndNestedCalls() {
        assertEquals(1, math("bit(4+4,1+2)"), 0);
        assertEquals(1, math("bit(bit(8,3),bit(0,0))"), 0);
        assertEquals(13, math("2+10*bit(8,3)+bit(3,0)"), 0);
        assertEquals(1, math("bit(10-2*3-1,0)"), 0);
        assertEquals(0, math("bit(10-2*3-1,2)"), 0);
        assertEquals("bit({@flags},3)+2", checker.checkMath(" bit( {@flags}, 3 ) + 2 "));
    }

    @Test public void returnsNumberThatCanBeComparedInLogicalConditions() {
        String condition = "bit(8,3)=1&bit(255,8)=0|bit(0,0)=1";
        assertEquals(condition, checker.checkLogic(condition));
        assertTrue(ExpressionCalculator.calculateConditional(parse(condition)));
        assertFalse(ExpressionCalculator.calculateConditional(parse("bit(8,2)=1")));
        assertEquals("1>0&bit(8,3)+1=2", checker.checkLogic("1>0&bit(8,3)+1=2"));
        assertTrue(ExpressionCalculator.calculateConditional(parse("1>0&bit(8,3)+1=2")));
        assertThrows(IllegalArgumentException.class, () -> checker.checkLogic("bit(8,3)"));
        assertThrows(IllegalArgumentException.class, () -> checker.checkMath("bit(8,3)=1"));
    }

    @Test public void acceptsFunctionOnRightOfComparison() {
        for (String condition : new String[]{"1=bit(8,3)", "0<bit(1,0)", "1>=bit(1,0)",
                "0!=bit(1,0)", "bit(1,0)=bit(3,1)"}) {
            assertEquals(condition, checker.checkLogic(condition));
            assertTrue(ExpressionCalculator.calculateConditional(parse(condition)));
        }
    }

    @Test public void rejectsInvalidRuntimeNumbersBeforeLongConversion() {
        List<EToken> expression = parse("bit({@number},0)");
        double[] invalid = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                1.5, -1.5, Math.nextUp(9007199254740992d), Math.nextDown(-9007199254740992d),
                (double) Long.MAX_VALUE, (double) Long.MIN_VALUE};
        for (double value : invalid) {
            assertThrows("value " + value, IllegalArgumentException.class,
                    () -> ExpressionCalculator.calculateMath(expression,
                            t -> t.is(EType.PARAMETER) ? value : t.getValue()));
        }
    }

    @Test public void rejectsInvalidRuntimeIndicesWithoutJavaShiftMasking() {
        List<EToken> expression = parse("bit(1,{@index})");
        double[] invalid = {-1, 64, 65, 1.5, Double.NaN, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, 9007199254740992d};
        for (double value : invalid) {
            assertThrows("index " + value, IllegalArgumentException.class,
                    () -> ExpressionCalculator.calculateMath(expression,
                            t -> t.is(EType.PARAMETER) ? value : t.getValue()));
        }
        assertEquals(1, ExpressionCalculator.calculateMath(expression,
                t -> t.is(EType.PARAMETER) ? -0.0 : t.getValue()), 0);
    }

    @Test public void rejectsInvalidKnownArgumentsEvenWithUnknownOtherArgument() {
        String[] invalid = {"bit(1.5,0)", "bit(1,1.5)", "bit(1,64)", "bit(1,(0-1))",
                "bit(9007199254740994,0)", "bit((0-9007199254740994),0)",
                "bit(1/0,0)", "bit(0/0,0)", "bit(1,0/0)", "bit(1,1/0)",
                "bit({@flags},64)", "bit(1.5,{@index})", "bit(1,{@index}+bit(1,64))"};
        for (String expression : invalid) {
            assertThrows(expression, IllegalArgumentException.class, () -> checker.checkMath(expression));
            assertThrows(expression, IllegalArgumentException.class, () -> checker.checkLogic(expression + "=1"));
        }
    }

    @Test public void defersUnknownValuesUntilCalculation() {
        String[] valid = {"bit({@flags},{@index})", "bit(8,1/{@index})",
                "bit({@flags}/({@divisor}+1),{@index})", "bit({#speed},{@index})",
                "bit({sensor name},0)", "bit({x},0)", "bit({@NaN},0)"};
        for (String expression : valid) {
            assertEquals(expression, checker.checkMath(expression));
            assertEquals(expression + "=1", checker.checkLogic(expression + "=1"));
        }
    }

    @Test public void rejectsMalformedCallsInParserAndChecker() {
        String[] invalid = {"bit", "bit()", "bit(1)", "bit(1,)", "bit(,1)", "bit(1,,2)",
                "bit(1,2,3)", "bit1,2", "bit((1,2),0)", "bit(1,2", "bit(1,2))",
                "(bit(1,2)", "bit(1,2),0", "1,2", "bit(1,2)3", "2bit(1,2)",
                "bit(1,2)+", "bit(1+,2)", "bit(1,2)(3)", "bit(1,bit(2,))"};
        for (String expression : invalid) {
            assertThrows(expression, IllegalArgumentException.class, () -> parse(expression));
            assertThrows(expression, IllegalArgumentException.class, () -> checker.checkMath(expression));
        }
    }

    @Test public void rejectsBooleanArgumentsAndNumericLogicalOperands() {
        for (String expression : new String[]{"bit(1>0,0)", "bit(1,(1=1))", "bit(1,0)&1=1",
                "bit(1,0)|bit(1,0)", "(1>0)+bit(1,0)", "bit(1,0)<1<2"}) {
            assertThrows(expression, IllegalArgumentException.class, () -> parse(expression));
            assertThrows(expression, IllegalArgumentException.class, () -> checker.checkLogic(expression));
        }
    }

    @Test public void preservesLogicalEvaluationWithoutShortCircuit() {
        List<EToken> expression = parse("1=1|bit({@flags},0)=1");
        assertThrows(IllegalArgumentException.class, () -> ExpressionCalculator.calculateConditional(expression,
                t -> t.is(EType.PARAMETER) ? Double.NaN : t.getValue()));
    }

    @Test public void retainsLegacyAssignedTokenValues() {
        List<EToken> expression = parse("bit({@flags},3)");
        expression.stream().filter(t -> t.is(EType.PARAMETER)).forEach(t -> t.setValue(8));
        assertEquals(1, ExpressionCalculator.calculateMath(expression), 0);
    }

    private static List<EToken> parse(String expression) {
        return ReversePolishNotation.create(expression, List.of());
    }

    private static double math(String expression) {
        return ExpressionCalculator.calculateMath(parse(expression));
    }
}
