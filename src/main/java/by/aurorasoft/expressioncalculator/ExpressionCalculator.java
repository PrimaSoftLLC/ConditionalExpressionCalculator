package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;

import java.util.List;
import java.util.Stack;

/**
 * calculate math or conditional or logic expression.
 * {@link SupportedOperations} see more about supported operations and them priority
 */
public class ExpressionCalculator {

    private final ReversePolishNotation reversePolishNotation = new ReversePolishNotation();

    /**
     * Calculate math {@param expression}. Every {@param expression} convert
     * to Reverse Polish Notation {@link ReversePolishNotation}
     * @return number as a result
     */
    public float calculateMath(String expression) {
        return calculateMath(reversePolishNotation.create(expression));
    }

    /**
     * Calculate math {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     * @return number as a result
     */
    public float calculateMath(List<EToken> tokens) {
        Stack<Float> res = new Stack<>();
        for (EToken t : tokens) {
            if (!SupportedOperations.isMath(t.getType())) {
                res.push(t.getValue());
            } else {
                float b = res.pop();
                float a = res.pop();
                res.push(SupportedOperations.calculate(a, b, t.getType()));
            }
        }
        return res.pop();
    }

    /**
     * Calculate conditional {@param expression}. Every {@param expression} convert
     * to Reverse Polish Notation {@link ReversePolishNotation}
     * @return boolean as a result
     */
    public boolean calculateConditional(String expression) {
        return calculateConditional(reversePolishNotation.create(expression));
    }

    /**
     * Calculate conditional {@param expression} the Reverse Polish Notation {@link ReversePolishNotation}
     * @return boolean as a result
     */
    public boolean calculateConditional(List<EToken> tokens) {
        final Result result = new Result();
        for (EToken t : tokens) {
            if (SupportedOperations.contains(t.getType())) {
                result.doOperation(t);
            } else {
                result.pushValue(t.getValue());
            }
        }
        return result.getResult();
    }

    private final class Result {
        Boolean result = null;
        Boolean tempResult = null;
        final Stack<Float> stack = new Stack();

        boolean getResult(){
            if(result != null){
                return result;
            } else {
                return tempResult != null? tempResult : true;
            }
        }

        void pushValue(float v) {
            stack.push(v);
        }

        void doOperation(EToken operand) {
            if (SupportedOperations.isMath(operand.getType())) {
                float b = stack.pop();
                float a = stack.pop();
                stack.push(SupportedOperations.calculate(a, b, operand.getType()));
                return;
            }
            if (SupportedOperations.isCondition(operand.getType())) {
                float b = stack.pop();
                float a = stack.pop();
                result = tempResult;
                tempResult = SupportedOperations.doCondition(a, b, operand.getType());
                return;
            }
            if (SupportedOperations.isLogic(operand.getType())) {
                if(result == null){
                    result = tempResult;
                }
                result = SupportedOperations.doLogic(result, tempResult, operand.getType());
                return;
            }
            throw new UnsupportedOperationException(String.format("operand: %s is not supported.", operand));
        }
    }
}
