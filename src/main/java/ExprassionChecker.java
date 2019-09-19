import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * check the expression
 * operations {@link #OPERATIONS}
 * RULES:
 * count of {@link SupportedOperations#OPEN_BRACKET} should be equals {@link SupportedOperations#CLOSE_BRACKET}
 * count of numbers should be equals Operators - 1
 * after a number => (operations or {@link SupportedOperations#CLOSE_BRACKET})
 * after an operation => (number or {@link SupportedOperations#OPEN_BRACKET})
 * after an {@link SupportedOperations#OPEN_BRACKET} => (number or {@link SupportedOperations#OPEN_BRACKET})
 * after an {@link SupportedOperations#CLOSE_BRACKET} => (operation or {@link SupportedOperations#CLOSE_BRACKET})
 */
public class ExprassionChecker {

    private static final Set<String> OPERATIONS = new HashSet<>();
    private static final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern sensorTokenPattern = Pattern.compile("\\{\\d+}");

    private final ReversePolishNotationStack RPNcreator = new ReversePolishNotationStack();

    static {
        OPERATIONS.addAll(SupportedOperations.MATH);
        OPERATIONS.addAll(SupportedOperations.CONDITIONALS);
        OPERATIONS.addAll(SupportedOperations.LOGIC);
    }

    /**
     * @throws IllegalArgumentException if {@param raw} is not valid matematical expression
     */
    public void check(String raw) {
        final ExprassionIterator iterator = new ExprassionIterator(raw);
        final Counter counter = new Counter();
        while (iterator.hasNext()) {
            Operand current = counter.count(iterator.next());
            if (iterator.hasNext()) {
                Operand next = counter.count(iterator.next());
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
        if (isLogicExpression(raw)) {
            conditionalCheck(raw);
        }
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

    private void conditionalCheck(String raw) {
        Deque<String> stack = RPNcreator.create(raw);
        Iterator<String> iterator = stack.iterator();
        String prev = "prev";
        while(iterator.hasNext()){
            String current = iterator.next();
            if((SupportedOperations.isCondition(prev) || SupportedOperations.isLogic(prev)) && SupportedOperations.isMath(current)){
                throw new IllegalArgumentException(String.format("Illegal logic expression - result is not boolean. " +
                        "Wrong use operands '%s' and '%s'", prev, current));
            }
            prev = current;
        }
        if (!SupportedOperations.isLogic(stack.getLast()) && !SupportedOperations.isCondition(stack.getLast())) {
            throw new IllegalArgumentException(
                    String.format("Illegal logic expression - result is not boolean. Wrong use operand '%s'",
                            stack.getLast()));
        }
    }

    private boolean isLogicExpression(String raw) {
        for (String c : SupportedOperations.LOGIC) {
            if (raw.contains(c)) {
                return true;
            }
        }

        for (String c : SupportedOperations.CONDITIONALS) {
            if (raw.contains(c)) {
                return true;
            }
        }
        return false;
    }

    private enum Operand {
        OPERATION, OPEN_BRACKET, CLOSE_BRACKET, NUMBER
    }

    private class Counter {

        int countOpenBracket = 0;
        int countCloseBracket = 0;
        int countNumbers = 0;
        int countOperations = 0;

        Operand count(String r) {
            if (OPERATIONS.contains(r)) {
                countOperations++;
                return Operand.OPERATION;
            }
            if (isNumeric(r)) {
                countNumbers++;
                return Operand.NUMBER;
            }
            if (r.equals(SupportedOperations.OPEN_BRACKET)) {
                countOpenBracket++;
                return Operand.OPEN_BRACKET;
            }
            if (r.equals(SupportedOperations.CLOSE_BRACKET)) {
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

        //todo unite numberPattern and sensorTokenPattern in one regexp
        boolean isNumeric(String strNum) {
            Matcher numb = numberPattern.matcher(strNum);
            Matcher sensor = sensorTokenPattern.matcher(strNum);
            return numb.matches() || sensor.matches();
        }
    }
}
