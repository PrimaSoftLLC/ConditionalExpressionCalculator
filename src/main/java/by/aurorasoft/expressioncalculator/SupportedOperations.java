package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SupportedOperations {

    private static final long MAX_EXACT_INTEGER = 1L << 53;

    public static final String MESSAGE_PARAMS_KEY_VALUE_SEPARATOR = ":";

    public static final char MESSAGE_PROPERTY_TOKEN = '#';

    public static final char MESSAGE_PARAMETER_TOKEN = '@';

    public static final Set<String> AVAILABLE_PARAMS = Set.of("latitude", "longitude", "altitude", "speed",
            "amountSatellite", "course", "gpsOdometer", "gsmStrength", "battery", "batteryVoltage", "onboardVoltage",
            "ecoAcceleration", "ecoBraking", "ecoCornering", "ecoBump");

    public static final char START_SENSOR_ID_TOKEN = '{';
    public static final char END_SENSOR_ID_TOKEN = '}';

    public static final Set<EType> NUMBERABLE_TYPES = Set.of(EType.SENSOR_ID, EType.NUMB, EType.PROPERTY, EType.PARAMETER);

    public static final Map<EType, Integer> FUNCTION_ARITIES = Map.of(
            EType.BIT, 2, EType.BIT_AND, 2, EType.BIT_OR, 2, EType.BIT_XOR, 2,
            EType.BIT_NOT, 1, EType.BIT_SHL, 2, EType.BIT_SHR, 2, EType.BIT_USHR, 2);

    public static final Set<EType> MATH_TYPES = Set.of(EType.MULT, EType.DIV, EType.PLUS, EType.MINUS,
            EType.BIT, EType.BIT_AND, EType.BIT_OR, EType.BIT_XOR, EType.BIT_NOT,
            EType.BIT_SHL, EType.BIT_SHR, EType.BIT_USHR);

    public static final Set<EType> CONDITIONALS_TYPES = Set.of(EType.MORE, EType.MORE_E, EType.LESS, EType.LESS_E, EType.EQUAL, EType.NOT_E);

    public static final Set<EType> LOGIC_TYPES = Set.of(EType.OR, EType.AND);

    public static final Set<Character> UNO_OPERATIONS = Set.of(
            EType.PLUS.detail.charAt(0), EType.MINUS.detail.charAt(0), EType.MULT.detail.charAt(0),
            EType.DIV.detail.charAt(0), EType.OPEN_B.detail.charAt(0), EType.CLOSE_B.detail.charAt(0),
            EType.OR.detail.charAt(0), EType.AND.detail.charAt(0), EType.COMMA.detail.charAt(0));

    public static final Map<EType, Integer> PRIORITY = new HashMap<>();
    
    static {
        PRIORITY.put(EType.MULT, 4);
        PRIORITY.put(EType.DIV, 4);
        PRIORITY.put(EType.PLUS, 3);
        PRIORITY.put(EType.MINUS, 3);
        PRIORITY.put(EType.MORE, 2);
        PRIORITY.put(EType.MORE_E, 2);
        PRIORITY.put(EType.LESS, 2);
        PRIORITY.put(EType.LESS_E, 2);
        PRIORITY.put(EType.EQUAL, 2);
        PRIORITY.put(EType.NOT_E, 2);
        PRIORITY.put(EType.AND, 1);
        PRIORITY.put(EType.OR, 1);
        PRIORITY.put(EType.OPEN_B, 0);
        PRIORITY.put(EType.CLOSE_B, 0);
    }

    public static boolean contains(EType operation) {
        return PRIORITY.containsKey(operation);
    }

    public static boolean isCondition(EType operation){
        return CONDITIONALS_TYPES.contains(operation);
    }

    public static boolean isLogic(EType operation){
        return LOGIC_TYPES.contains(operation);
    }

    public static boolean isMath(EType operation){
        return MATH_TYPES.contains(operation);
    }

    static boolean isFunction(EType operation) {
        return FUNCTION_ARITIES.containsKey(operation);
    }

    static int arity(EType operation) {
        return FUNCTION_ARITIES.getOrDefault(operation, 2);
    }

    public static double calculate(double a, double b, EType operand){
        if (isFunction(operand)) {
            checkFunctionArgument(a, operand, 0);
            checkFunctionArgument(b, operand, 1);
            long left = (long) a;
            long right = (long) b;
            long result;
            switch (operand) {
                case BIT: result = (left >>> (int) b) & 1L; break;
                case BIT_AND: result = left & right; break;
                case BIT_OR: result = left | right; break;
                case BIT_XOR: result = left ^ right; break;
                case BIT_SHL: result = left << (int) b; break;
                case BIT_SHR: result = left >> (int) b; break;
                case BIT_USHR: result = left >>> (int) b; break;
                default: throw new UnsupportedOperationException("Not a binary function: " + operand);
            }
            return checkedBitResult(result);
        }
        switch (operand){
            case PLUS: return a + b;
            case MINUS: return a - b;
            case MULT: return a * b;
            case DIV: return a / b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }

    public static double calculate(double value, EType operand) {
        if (operand != EType.BIT_NOT) {
            throw new UnsupportedOperationException("Not a unary function: " + operand);
        }
        checkBitNumber(value);
        return checkedBitResult(~(long) value);
    }

    static void checkFunctionArgument(double value, EType function, int index) {
        if (index == 1 && (function == EType.BIT || function == EType.BIT_SHL
                || function == EType.BIT_SHR || function == EType.BIT_USHR)) {
            checkBitIndex(value);
        } else {
            checkBitNumber(value);
        }
    }

    private static double checkedBitResult(long result) {
        // Check the long before conversion: e.g. -(2^53 + 1) rounds to -2^53 as double.
        if (result < -MAX_EXACT_INTEGER || result > MAX_EXACT_INTEGER) {
            throw new IllegalArgumentException("Bitwise result must be in [-2^53, 2^53]: " + result);
        }
        return result;
    }

    static void checkBitNumber(double value) {
        if (!Double.isFinite(value) || value != Math.rint(value) || Math.abs(value) > MAX_EXACT_INTEGER) {
            throw new IllegalArgumentException("bit number must be a finite integer in [-2^53, 2^53]: " + value);
        }
    }

    static void checkBitIndex(double index) {
        if (!Double.isFinite(index) || index != Math.rint(index) || index < 0 || index > 63) {
            throw new IllegalArgumentException("bit index must be a finite integer in [0, 63]: " + index);
        }
    }

    public static boolean doCondition(double a, double b, EType operand){
        switch (operand){
            case MORE: return a > b;
            case MORE_E: return a >= b;
            case LESS: return a < b;
            case LESS_E: return a <= b;
            case EQUAL: return a == b;
            case NOT_E: return a != b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }

    public static boolean doLogic(boolean a, boolean b, EType operand){
        switch (operand){
            case OR: return a || b;
            case AND: return a && b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }
}
