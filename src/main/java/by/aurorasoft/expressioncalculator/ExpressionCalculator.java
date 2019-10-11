package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;

import java.util.List;

/**
 * calculate math or conditional or logic expression.
 * {@link SupportedOperations} see more about supported operations and them priority
 */
public class ExpressionCalculator {

    private final ReversePolishNotation reversePolishNotation = new ReversePolishNotation();

    /**
     * Calculate math {@param expression}. Every {@param expression} convert
     * to Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return number as a result
     */
    public float calculateMath(String expression) {
        return calculateMath(reversePolishNotation.create(expression));
    }

    /**
     * Calculate math {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return number as a result
     */
    public float calculateMath(List<EToken> tokens) {
        final float[] stack = new float[tokens.size()];
        int i = 0;
        for (EToken t : tokens) {
            if (t.is(EType.TOKEN) || t.is(EType.NUMB)) {
                stack[i++] = t.getValue();
            } else {
                float b = stack[i-- - 1];
                float a = stack[i-- - 1];
                stack[i++] = SupportedOperations.calculate(a, b, t.getType());
            }
        }
        return stack[i-- - 1];
    }

    /**
     * Calculate conditional {@param expression}. Every {@param expression} convert
     * to Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return boolean as a result
     */
    public boolean calculateConditional(String expression) {
        return calculateConditional(reversePolishNotation.create(expression));
    }

    /**
     * Calculate conditional {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return boolean as a result
     */
    public boolean calculateConditional(List<EToken> tokens) {
        Boolean result = null;
        Boolean tempResult = null;
        int i = 0;
        final float[] stack = new float[tokens.size()];
        for (EToken t : tokens) {
            if (t.is(EType.TOKEN) || t.is(EType.NUMB)) {
                stack[i++] = t.getValue();
                continue;
            }
            if (SupportedOperations.isMath(t.getType())) {
                float b = stack[i-- - 1];
                float a = stack[i-- - 1];
                stack[i++] = SupportedOperations.calculate(a, b, t.getType());
                continue;
            }
            if (SupportedOperations.isCondition(t.getType())) {
                float b = stack[i-- - 1];
                float a = stack[i-- - 1];
                result = tempResult;
                tempResult = SupportedOperations.doCondition(a, b, t.getType());
                continue;
            }
            if (SupportedOperations.isLogic(t.getType())) {
                if (result == null) {
                    result = tempResult;
                }
                result = SupportedOperations.doLogic(result, tempResult, t.getType());
                continue;
            }
            throw new IllegalArgumentException(String.format("Illegal token: '%s'", t.getType().detail));
        }
        return result != null ? result : tempResult != null? tempResult: true;
    }

    public static void main(String[] args) {
        List<EToken> condition = new ReversePolishNotation().create("200.899>300.987");
        ExpressionCalculator c = new ExpressionCalculator();
        long s = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            c.calculateConditional(condition);
        }
        System.out.println("ms: " + (System.currentTimeMillis() - s));
    }
}
