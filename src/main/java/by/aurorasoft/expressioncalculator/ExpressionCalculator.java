package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;

import java.util.List;

/**
 * calculate math or conditional or logic expression.
 * {@link SupportedOperations} see more about supported operations and them priority
 */
public class ExpressionCalculator {

    /**
     * Calculate math {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return number as a result
     */
    public static double calculateMath(List<EToken> tokens) {
        final double[] stack = new double[tokens.size()];
        int i = 0;
        for (EToken t : tokens) {
            if (SupportedOperations.NUMBERABLE_TYPES.contains(t.getType())) {
                stack[i++] = t.getValue();
            } else {
                double b = stack[i-- - 1];
                double a = stack[i-- - 1];
                stack[i++] = SupportedOperations.calculate(a, b, t.getType());
            }
        }
        return stack[i - 1];
    }

    /**
     * Calculate conditional {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return boolean as a result
     */
    public static boolean calculateConditional(List<EToken> tokens) {
        int i = 0;
        final double[] stack = new double[tokens.size()];
        for (EToken t : tokens) {
            if (SupportedOperations.NUMBERABLE_TYPES.contains(t.getType())) {
                stack[i++] = t.getValue();
                continue;
            }
            if (SupportedOperations.isMath(t.getType())) {
                double b = stack[i-- - 1];
                double a = stack[i-- - 1];
                stack[i++] = SupportedOperations.calculate(a, b, t.getType());
                continue;
            }
            if (SupportedOperations.isCondition(t.getType())) {
                double b = stack[i-- - 1];
                double a = stack[i-- - 1];
                boolean tempResult = SupportedOperations.doCondition(a, b, t.getType());
                stack[i++] = tempResult ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                continue;
            }
            if (SupportedOperations.isLogic(t.getType())) {
                boolean b = stack[i-- - 1] == Integer.MAX_VALUE;
                boolean a = stack[i-- - 1] == Integer.MAX_VALUE;
                boolean tempResult = SupportedOperations.doLogic(a, b, t.getType());
                stack[i++] = tempResult ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                continue;
            }
            throw new IllegalArgumentException(String.format("Illegal token: '%s'", t.getType().detail));
        }
        return stack[0] == Integer.MAX_VALUE;
    }
}
