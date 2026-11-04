package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;

import java.util.ArrayList;
import java.util.List;

/** Parses function expressions into the same numeric/boolean RPN used by the calculator. */
final class FunctionExpressionParser {
    private final List<EToken> input;
    private final List<EToken> output = new ArrayList<>();
    private int position;

    FunctionExpressionParser(List<EToken> input) {
        this.input = input;
    }

    List<EToken> parse() {
        expression(1);
        if (position != input.size()) {
            throw error("Unexpected token");
        }
        return output;
    }

    /** Returns true for numeric expressions, false for boolean expressions. */
    private boolean expression(int minimumPriority) {
        boolean numeric = primary();
        while (position < input.size()) {
            EToken operation = input.get(position);
            Integer priority = SupportedOperations.PRIORITY.get(operation.getType());
            if (priority == null || priority < minimumPriority) {
                break;
            }
            position++;
            boolean rightNumeric = expression(priority + 1);
            if (SupportedOperations.isLogic(operation.getType())) {
                if (numeric || rightNumeric) {
                    throw error("Logical operators require boolean operands");
                }
            } else {
                if (!numeric || !rightNumeric) {
                    throw error("Arithmetic and comparisons require numeric operands");
                }
                numeric = SupportedOperations.isMath(operation.getType());
            }
            output.add(operation);
        }
        return numeric;
    }

    private boolean primary() {
        if (position == input.size()) {
            throw error("Expected an operand");
        }
        EToken token = input.get(position++);
        if (SupportedOperations.NUMBERABLE_TYPES.contains(token.getType())) {
            output.add(token);
            return true;
        }
        if (token.is(EType.OPEN_B)) {
            boolean numeric = expression(1);
            expect(EType.CLOSE_B);
            return numeric;
        }
        if (token.is(EType.BIT)) {
            expect(EType.OPEN_B);
            if (!expression(1)) {
                throw error("bit number must be numeric");
            }
            expect(EType.COMMA);
            if (!expression(1)) {
                throw error("bit index must be numeric");
            }
            expect(EType.CLOSE_B);
            output.add(token);
            return true;
        }
        throw error("Expected a number, reference, parenthesis or bit call");
    }

    private void expect(EType type) {
        if (position == input.size() || !input.get(position).is(type)) {
            throw error("Expected '" + type.detail + "'");
        }
        position++;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " at token " + position);
    }
}
