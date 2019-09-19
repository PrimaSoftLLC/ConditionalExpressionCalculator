package by.aurorasoft.expressioncalculator;

import java.util.Deque;
import java.util.Stack;

/**
 * calculate math or conditional or logic expression.
 * {@link SupportedOperations} see more about supported operations and them priority
 */
public class ExpressionCalculator {

    private final ReversePolishNotationStack reversePolishNotation = new ReversePolishNotationStack();

    /**
     * Calculate math {@param expression}. Every {@param expression} convert
     * to Reverse Polish Notation {@link ReversePolishNotationStack}
     * @return number as a result
     */
    public float calculateMath(String expression) {
        return calculateMath(reversePolishNotation.create(expression));
    }

    /**
     * Calculate math {@param expression} the Reverse Polish Notation {@link ReversePolishNotationStack}
     * @return number as a result
     */
    public float calculateMath(Deque<String> deque) {
        Stack<Float> res = new Stack<>();
        while (deque.size() > 0) {
            String operand = deque.pollFirst();
            if (!SupportedOperations.isMath(operand)) {
                res.push(Float.valueOf(operand));
            } else {
                float b = res.pop();
                float a = res.pop();
                res.push(SupportedOperations.calculate(a, b, operand));
            }
        }
        return res.pop();
    }

    /**
     * Calculate conditional {@param expression}. Every {@param expression} convert
     * to Reverse Polish Notation {@link ReversePolishNotationStack}
     * @return boolean as a result
     */
    public boolean calculateConditional(String expression) {
        return calculateConditional(reversePolishNotation.create(expression));
    }

    /**
     * Calculate conditional {@param expression} the Reverse Polish Notation {@link ReversePolishNotationStack}
     * @return boolean as a result
     */
    public boolean calculateConditional(Deque<String> deque) {
        Result result = new Result();
        while (deque.size() > 0) {
            String operand = deque.pollFirst();
            if (!SupportedOperations.contains(operand)) {
                result.pushValue(Float.valueOf(operand));
            } else {
                result.doOperation(operand);
            }
        }
        return result.getResult();
    }

    private final class Result {
        Boolean result = null;
        boolean tempResult;
        Stack<Float> stack = new Stack();

        boolean getResult(){
            if(result != null){
                return result;
            } else {
                return tempResult;
            }
        }

        void pushValue(float v) {
            stack.push(v);
        }

        void doOperation(String operand) {
            if (SupportedOperations.isMath(operand)) {
                float b = stack.pop();
                float a = stack.pop();
                stack.push(SupportedOperations.calculate(a, b, operand));
                return;
            }
            if (SupportedOperations.isCondition(operand)) {
                float b = stack.pop();
                float a = stack.pop();
                tempResult = SupportedOperations.doCondition(a, b, operand);
                return;
            }
            if (SupportedOperations.isLogic(operand)) {
                if(result == null){
                    result = tempResult;
                }
                result = SupportedOperations.doLogic(result, tempResult, operand);
                return;
            }
            throw new UnsupportedOperationException(String.format("operand: %s is not supported.", operand));
        }
    }
}
