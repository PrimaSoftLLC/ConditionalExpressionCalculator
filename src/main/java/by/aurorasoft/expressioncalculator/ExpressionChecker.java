package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.ETokens;
import by.aurorasoft.expressioncalculator.model.EType;
import by.aurorasoft.expressioncalculator.model.sensor.SensorI;
import by.aurorasoft.expressioncalculator.model.sensor.impl.SensorImpl;

import java.util.*;

/**
 * check the expression
 * operations {@link #OPERATIONS}
 * RULES:
 * count of OPEN_BRACKET should be equals CLOSE_BRACKET
 * count of numbers should be equals Operators - 1
 * after a number => (operations or CLOSE_BRACKET)
 * after an operation => (number or OPEN_BRACKET)
 * after an OPEN_BRACKET => (number or OPEN_BRACKET)
 * after an CLOSE_BRACKET => (operation or CLOSE_BRACKET)
 */
public class ExpressionChecker {

    private static final Set<EType> OPERATIONS = new HashSet<>();

    static {
        OPERATIONS.addAll(SupportedOperations.MATH_TYPES);
        OPERATIONS.addAll(SupportedOperations.CONDITIONALS_TYPES);
        OPERATIONS.addAll(SupportedOperations.LOGIC_TYPES);
    }

    /**
     * @return return corrected expression
     * @throws IllegalArgumentException if {@param raw} is not valid mathematical expression
     */
    public String checkMath(String raw) {
        if (raw == null) {
            return null;
        }
        raw = removeRedundantSpaces(raw);
        if (raw.length() == 0) {
            return null;
        }
        List<SensorI> dummyRawTokens = createDummyRawTokenList(raw);
        if (checkFunctionExpression(raw, dummyRawTokens, false)) {
            return raw;
        }
        checkCounts(raw, dummyRawTokens);
        List<EToken> tokens = ReversePolishNotation.create(raw, dummyRawTokens);
        for (EToken t : tokens) {
            if (SupportedOperations.isLogic(t.getType()) || SupportedOperations.isCondition(t.getType())) {
                throw new IllegalArgumentException(String.format("Illegal operation '%s' ", t.getType().detail));
            }
        }

        try {
            ExpressionCalculator.calculateMath(tokens);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong math expression");
        }
        return raw;
    }

    /**
     * Spaces available only into braces '{' '}' in another places spaces deleted
     * "{sensor one} + {sensor two} - 2" to "{sensor one}+{sensor two}-2"
     */
    private String removeRedundantSpaces(String raw) {
        StringBuilder stringBuilder = new StringBuilder(raw.trim());
        boolean isSensorToken = false;
        for (int i = 0; i < stringBuilder.length(); i++) {
            char ch = stringBuilder.charAt(i);
            if (ch == SupportedOperations.START_SENSOR_ID_TOKEN) {
                isSensorToken = true;
                continue;
            }
            if (ch == SupportedOperations.END_SENSOR_ID_TOKEN) {
                isSensorToken = false;
                continue;
            }
            if (!isSensorToken && ch == ' ') {
                stringBuilder.deleteCharAt(i--);
            }
        }
        return stringBuilder.toString();
    }

    private List<SensorI> createDummyRawTokenList(String raw) {
        List<SensorI> rawTokens = new ArrayList<>();
        long dummyId = 1;
        int index = 0;
        while (index < raw.length() - 1) {
            StringBuilder r = new StringBuilder();
            if (raw.charAt(index) == SupportedOperations.START_SENSOR_ID_TOKEN) {
                index++; // skip '{'
                while (index <= raw.length() - 1 && raw.charAt(index) != SupportedOperations.END_SENSOR_ID_TOKEN) {
                    r.append(raw.charAt(index++));
                }
                // skip '}'
                if (index > raw.length() - 1) {
                    index--;
                }
                if (raw.charAt(index++) != '}') {
                    throw new IllegalArgumentException(String.format("Wrong expression: %s not found closing '}'", r));
                }
                rawTokens.add(new SensorImpl(dummyId++, r.toString(), 1L, null, null, null, Map.of()));
            } else {
                index++;
            }
        }
        return rawTokens;
    }

    /**
     * @return return corrected expression
     * @throws IllegalArgumentException if {@param raw} is not valid logic expression
     */
    public String checkLogic(String raw) {
        if (raw == null) {
            return null;
        }
        raw = removeRedundantSpaces(raw);
        if (raw.length() == 0) {
            return null;
        }
        List<SensorI> dummyTokens = createDummyRawTokenList(raw);
        if (checkFunctionExpression(raw, dummyTokens, true)) {
            return raw;
        }
        checkCounts(raw, dummyTokens);
        conditionalCheck(raw, dummyTokens);
        return raw;
    }

    private boolean checkFunctionExpression(String raw, List<SensorI> rawTokens, boolean conditional) {
        ExpressionIterator iterator = new ExpressionIterator(raw, rawTokens);
        boolean hasFunctionSyntax = false;
        while (iterator.hasNext()) {
            EToken token = iterator.nextEToken();
            hasFunctionSyntax |= token.is(EType.BIT) || token.is(EType.COMMA);
        }
        if (!hasFunctionSyntax) {
            return false;
        }
        List<EToken> tokens = ReversePolishNotation.create(raw, rawTokens);
        EType resultType = tokens.get(tokens.size() - 1).getType();
        boolean booleanResult = SupportedOperations.isCondition(resultType) || SupportedOperations.isLogic(resultType);
        if (conditional != booleanResult) {
            throw new IllegalArgumentException(conditional ? "Condition must be boolean" : "Formula must be numeric");
        }

        // null means unknown until calculation, not a dummy sensor/parameter value.
        Double[] values = new Double[tokens.size()];
        int size = 0;
        for (EToken token : tokens) {
            if (SupportedOperations.NUMBERABLE_TYPES.contains(token.getType())) {
                values[size++] = token.is(EType.NUMB) ? token.getValue() : null;
                continue;
            }
            Double right = values[--size];
            Double left = values[--size];
            if (token.is(EType.BIT)) {
                if (left != null) SupportedOperations.checkBitNumber(left);
                if (right != null) SupportedOperations.checkBitIndex(right);
            }
            values[size++] = left != null && right != null && SupportedOperations.isMath(token.getType())
                    ? SupportedOperations.calculate(left, right, token.getType()) : null;
        }
        return true;
    }

    private void checkCounts(String raw, List<SensorI> rawTokens) {
        final ExpressionIterator iterator = new ExpressionIterator(raw, rawTokens);
        final Counter counter = new Counter();
        while (iterator.hasNext()) {
            EToken t = iterator.nextEToken();
            Operand current = counter.count(t);
            if (iterator.hasNext()) {
                Operand next = counter.count(iterator.nextEToken());
                grammarCheck(current, next, iterator.getIndex());
            } else {
                if (current != Operand.NUMBER && current != Operand.CLOSE_BRACKET) {
                    throw new IllegalArgumentException(
                            String.format("Illegal argument position: %s. Expression can only end on the %s or %s, but was '%s'",
                                    iterator.getIndex(), Operand.CLOSE_BRACKET, Operand.NUMBER, current));
                }
            }
        }
        counter.checkCounts();
    }

    private void grammarCheck(Operand current, Operand next, int index) {
        switch (current) {
            case NUMBER:
                if (next != Operand.OPERATION && next != Operand.CLOSE_BRACKET) {
                    throw new IllegalArgumentException(
                            String.format("Illegal argument position: %s after '%s' should be '%s' or '%s', but was '%s'",
                                    index, Operand.NUMBER, Operand.OPERATION, Operand.CLOSE_BRACKET, next));
                }
                break;
            case CLOSE_BRACKET:
                if (next != Operand.OPERATION && next != Operand.CLOSE_BRACKET) {
                    throw new IllegalArgumentException(
                            String.format("Illegal argument position: %s after '%s' should be '%s' or '%s', but was '%s'",
                                    index, Operand.CLOSE_BRACKET, Operand.OPERATION, Operand.CLOSE_BRACKET, next));
                }
                break;
            case OPERATION:
                if (next != Operand.NUMBER && next != Operand.OPEN_BRACKET) {
                    throw new IllegalArgumentException(
                            String.format("Illegal argument position: %s after '%s' should be '%s' or '%s', but was '%s'",
                                    index, Operand.OPERATION, Operand.NUMBER, Operand.OPEN_BRACKET, next));
                }
                break;
            case OPEN_BRACKET:
                if (next != Operand.NUMBER && next != Operand.OPEN_BRACKET) {
                    throw new IllegalArgumentException(
                            String.format("Illegal argument position: %s after '%s' should be '%s' or '%s', but was '%s'",
                                    index, Operand.OPEN_BRACKET, Operand.NUMBER, Operand.OPEN_BRACKET, next));
                }
                break;
        }
    }

    private void conditionalCheck(String raw, List<SensorI> rawTokens) {
        List<EToken> tokens = ReversePolishNotation.create(raw, rawTokens);
        EToken prev = ETokens.create(")");
        int logicCount = 0;
        int conditionalCount = 0;
        for (EToken t : tokens) {
            if ((SupportedOperations.isCondition(prev.getType()) || SupportedOperations.isLogic(prev.getType())) && SupportedOperations.isMath(t.getType())) {
                throw new IllegalArgumentException(String.format("Illegal logic expression - result is not boolean. " +
                        "Wrong use operands '%s' and '%s'", prev, t));
            }
            prev = t;

            if (SupportedOperations.isLogic(t.getType())) {
                logicCount++;
            }
            if (SupportedOperations.isCondition(t.getType())) {
                conditionalCount++;
            }
        }

        if (conditionalCount - 1 != logicCount) {
            throw new IllegalArgumentException("Wrong count of logic and conditional operators");
        }

        if (!SupportedOperations.isLogic(tokens.get(tokens.size() - 1).getType()) && !SupportedOperations.isCondition(tokens.get(tokens.size() - 1).getType())) {
            throw new IllegalArgumentException(
                    String.format("Illegal logic expression - result is not boolean. Wrong use operand '%s'",
                            tokens.get(tokens.size() - 1).getType().detail));
        }

        // check can calculate
        try {
            ExpressionCalculator.calculateConditional(tokens);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong logic expression");
        }
    }

    private enum Operand {
        OPERATION, OPEN_BRACKET, CLOSE_BRACKET, NUMBER
    }

    private static class Counter {

        int countOpenBracket = 0;
        int countCloseBracket = 0;
        int countNumbers = 0;
        int countOperations = 0;

        Operand count(EToken r) {
            if (OPERATIONS.contains(r.getType())) {
                countOperations++;
                return Operand.OPERATION;
            }
            if (SupportedOperations.NUMBERABLE_TYPES.contains(r.getType())) {
                countNumbers++;
                return Operand.NUMBER;
            }
            if (r.is(EType.OPEN_B)) {
                countOpenBracket++;
                return Operand.OPEN_BRACKET;
            }
            if (r.is(EType.CLOSE_B)) {
                countCloseBracket++;
                return Operand.CLOSE_BRACKET;
            }
            throw new IllegalArgumentException(String.format("Expression contains illegal argument: %s", r));
        }

        void checkCounts() {
            if (countOpenBracket != countCloseBracket) {
                throw new IllegalArgumentException("Count of open bracket don't equals to count of close brackets");
            }
            if (countNumbers - 1 != countOperations) {
                throw new IllegalArgumentException("Illegal count of numbers or operations");
            }
        }
    }
}
