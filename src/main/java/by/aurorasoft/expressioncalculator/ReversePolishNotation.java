package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.ETokens;
import by.aurorasoft.expressioncalculator.model.EType;
import by.aurorasoft.expressioncalculator.model.RawToken;

import java.util.*;

public class ReversePolishNotation {

    public static List<EToken> create(ExpressionIterator iterator) {
        final List<EToken> exitStack = new ArrayList<>();
        final Deque<EToken> operations = new LinkedList<>();
        while (iterator.hasNext()) {
            EToken t = iterator.nextEToken();
            convert(exitStack, operations, t);
        }
        resulting(exitStack, operations);
        return exitStack;
    }

    private static void convert(List<EToken> exitStack, Deque<EToken> operations, EToken t) {
        if (t.is(EType.OPEN_B)) {
            operations.addLast(t);
            return;
        }
        if (t.is(EType.CLOSE_B)) {
            while (!operations.getLast().is(EType.OPEN_B)) {
                exitStack.add(operations.pollLast());
            }
            operations.pollLast();
            return;
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

    private static void resulting(List<EToken> exitStack, Deque<EToken> operations) {
        while (operations.size() != 0) {
            exitStack.add(operations.pollLast());
        }
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
    public static List<EToken> create(String stringExpression, List<RawToken> rawTokens) {
        final ExpressionIterator rawExpr = new ExpressionIterator(stringExpression, rawTokens);
        return create(rawExpr);
    }

//    @Deprecated
//    private static List<EToken> create(String[] operands, List<RawToken> rawTokens) {
//        final List<EToken> exitStack = new ArrayList<>(operands.length);
//        int i = 0;
//        final EToken[] operations = new EToken[operands.length/2 + 1];
//        for (String s : operands) {
//            EToken t = ETokens.create(s);
//            if (t.is(EType.OPEN_B)) {
//                operations[i++] = t;
//                continue;
//            }
//            if (t.is(EType.CLOSE_B)) {
//                while (!operations[i - 1].is(EType.OPEN_B)) {
//                    exitStack.add(operations[i-- - 1]);
//                }
//                i--;
//                continue;
//            }
//            if (t.is(EType.NUMB) || t.is(EType.TOKEN)) {
//                exitStack.add(t);
//            } else {
//                if (i != 0 &&
//                        SupportedOperations.PRIORITY.get(operations[i - 1].getType()) >= SupportedOperations.PRIORITY.get(t.getType())) {
//                    exitStack.add(operations[i-- - 1]);
//                }
//                operations[i++] = t;
//            }
//        }
//        while (i != 0) {
//            exitStack.add(operations[i-- - 1]);
//        }
//        return exitStack;
//    }
}
