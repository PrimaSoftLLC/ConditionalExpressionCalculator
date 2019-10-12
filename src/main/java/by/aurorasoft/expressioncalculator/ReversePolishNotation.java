package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;

import java.util.*;

public class ReversePolishNotation {

    public static List<EToken> create(ExpressionIterator iterator) {
        final List<EToken> exitStack = new LinkedList<>();
        final Deque<EToken> operations = new LinkedList<>();

        while (iterator.hasNext()) {
            EToken t = iterator.next();
            if (t.is(EType.OPEN_B)) {
                operations.addLast(t);
                continue;
            }
            if (t.is(EType.CLOSE_B)) {
                while (!operations.getLast().is(EType.OPEN_B) ) {
                    exitStack.add(operations.pollLast());
                }
                operations.pollLast();
                continue;
            }
            if (!SupportedOperations.contains(t.getType())) {
                exitStack.add(t);
            } else {
                if (!operations.isEmpty() &&
                        SupportedOperations.PRIORITY.get(operations.getLast().getType()) >= SupportedOperations.PRIORITY.get(t.getType())) {
                    exitStack.add(operations.pollLast());
                }
                operations.addLast(t);
            }
        }
        while (operations.size() != 0) {
            exitStack.add(operations.pollLast());
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
    public static List<EToken> create(String stringExpression) {
        final ExpressionIterator rawExpr = new ExpressionIterator(stringExpression);
        return create(rawExpr);
    }
}
