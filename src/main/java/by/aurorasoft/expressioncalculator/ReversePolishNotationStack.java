package by.aurorasoft.expressioncalculator;

import java.util.*;

public class ReversePolishNotationStack {

    public Deque<String> create(ExprassionIterator iterator) {
        final Deque<String> exitStack = new LinkedList<>();
        final Deque<String> operations = new LinkedList<>();

        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.equals(SupportedOperations.OPEN_BRACKET)) {
                operations.addLast(s);
                continue;
            }
            if (s.equals(SupportedOperations.CLOSE_BRACKET)) {
                while (!operations.getLast().equals(SupportedOperations.OPEN_BRACKET)) {
                    exitStack.addLast(operations.pollLast());
                }
                operations.pollLast();
                continue;
            }
            if (!SupportedOperations.PRIORITY.containsKey(s)) {
                exitStack.addLast(s);
            } else {
                if (!operations.isEmpty() &&
                        SupportedOperations.PRIORITY.get(operations.getLast()) >= SupportedOperations.PRIORITY.get(s)) {
                    exitStack.addLast(operations.pollLast());
                }
                operations.addLast(s);
            }
        }

        while (operations.size() != 0) {
            exitStack.addLast(operations.pollLast());
        }

        return exitStack;
    }

    /**
     * More Information about reverse polish notation: {@see https://habr.com/ru/post/282379/}
     * prepare {@param stringExpression} to reverse polish notation. the method can work with math or/and conditional expressions
     * for example (6+10-4)/(1+1*2)+1, 10>0, {103}+10, {103}>10&&{104}+50<50.
     * supported operations:
     * priority: (, )
     * math: *, /, +, -
     * conditional: >, >=, <, <=, =, !=
     * logical: &, |
     * replace token: {long} Ex: {123}, {324}
     * more information about supported operations {@link SupportedOperations}
     *
     * @param stringExpression string contains math expression. Spaces are not allowed in the string.
     *                         Can contains dinamical data - token number in brackets {123}.
     * @return Stack of strings with reverse polish notation
     */
    public Deque<String> create(String stringExpression) {

        final ExprassionIterator rawExpr = new ExprassionIterator(stringExpression);
        return create(rawExpr);
    }
}
