package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;

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
            if (t.is(EType.TOKEN) || t.is(EType.NUMB) || t.is(EType.PARAM)) {
                stack[i++] = t.getValue();
            } else {
                double b = stack[i-- - 1];
                double a = stack[i-- - 1];
                stack[i++] = SupportedOperations.calculate(a, b, t.getType());
            }
        }
        return stack[i-- - 1];
    }

    /**
     * Calculate conditional {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     *
     * @return boolean as a result
     */
    public static boolean calculateConditional(List<EToken> tokens) {
        Boolean result = null;
        Boolean tempResult = null;
        int i = 0;
        final double[] stack = new double[tokens.size()];
        for (EToken t : tokens) {
            if (t.is(EType.TOKEN) || t.is(EType.NUMB) || t.is(EType.PARAM)) {
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
}
