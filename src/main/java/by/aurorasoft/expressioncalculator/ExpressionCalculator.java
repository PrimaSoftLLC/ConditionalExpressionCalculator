package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;
import by.aurorasoft.expressioncalculator.model.sensor.MessageI;
import by.aurorasoft.expressioncalculator.model.sensor.SensorI;

import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

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
        return calculateMath(tokens, EToken::getValue);
    }

    public static double calculateMath(List<EToken> tokens, ToDoubleFunction<EToken> valueResolver) {
        return calculateMath(tokens, valueResolver, null, 0, null);
    }

    static double calculateMath(List<EToken> tokens, MessageI message, double value, Map<Long, ? extends SensorI> sensors) {
        return calculateMath(tokens, null, message, value, sensors);
    }

    private static double calculateMath(List<EToken> tokens, ToDoubleFunction<EToken> resolver,
                                        MessageI message, double value, Map<Long, ? extends SensorI> sensors) {
        final double[] stack = new double[tokens.size()];
        int i = 0;
        for (EToken t : tokens) {
            if (SupportedOperations.NUMBERABLE_TYPES.contains(t.getType())) {
                stack[i++] = operand(t, resolver, message, value, sensors);
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
        return calculateConditional(tokens, EToken::getValue);
    }

    public static boolean calculateConditional(List<EToken> tokens, ToDoubleFunction<EToken> valueResolver) {
        return calculateConditional(tokens, valueResolver, null, 0, null);
    }

    static boolean calculateConditional(List<EToken> tokens, MessageI message, double value, Map<Long, ? extends SensorI> sensors) {
        return calculateConditional(tokens, null, message, value, sensors);
    }

    private static boolean calculateConditional(List<EToken> tokens, ToDoubleFunction<EToken> resolver,
                                               MessageI message, double value, Map<Long, ? extends SensorI> sensors) {
        int i = 0;
        final double[] stack = new double[tokens.size()];
        for (EToken t : tokens) {
            if (SupportedOperations.NUMBERABLE_TYPES.contains(t.getType())) {
                stack[i++] = operand(t, resolver, message, value, sensors);
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

    private static double operand(EToken token, ToDoubleFunction<EToken> resolver, MessageI message,
                                  double value, Map<Long, ? extends SensorI> sensors) {
        if (resolver != null) return resolver.applyAsDouble(token);
        if (token.is(EType.PROPERTY)) return message.getPropertyValue(token.getPropertyName());
        if (token.is(EType.PARAMETER)) return message.getParameterValue(token.getParameterName());
        if (token.is(EType.SENSOR_ID)) return token.getSensorId() == 0 ? value
                : sensors.get(token.getSensorId()).calculateValue(message, sensors);
        return token.getValue();
    }
}
