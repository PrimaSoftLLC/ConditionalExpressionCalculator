package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SupportedOperations {

    public static final char PARAM_TOKEN = '#';

    public static final Set<String> AVAILABLE_PARAMS = Set.of("latitude", "longitude", "altitude", "speed", "amount_satellite", "course");

    public static final char START_SENSOR_ID_TOKEN = '{';
    public static final char END_SENSOR_ID_TOKEN = '}';

    public static final Set<EType> MATH = Set.of(EType.MULT, EType.DIV, EType.PLUS, EType.MINUS);

    public static final Set<EType> CONDITIONALS = Set.of(EType.MORE, EType.MORE_E, EType.LESS, EType.LESS_E, EType.EQUAL, EType.NOT_E);

    public static final Set<EType> LOGIC = Set.of(EType.OR, EType.AND);

    public static final Set<Character> UNO_OPERATIONS = Set.of(
            EType.PLUS.detail.charAt(0), EType.MINUS.detail.charAt(0), EType.MULT.detail.charAt(0),
            EType.DIV.detail.charAt(0), EType.OPEN_B.detail.charAt(0), EType.CLOSE_B.detail.charAt(0),
            EType.OR.detail.charAt(0), EType.AND.detail.charAt(0));

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
        return CONDITIONALS.contains(operation);
    }

    public static boolean isLogic(EType operation){
        return LOGIC.contains(operation);
    }

    public static boolean isMath(EType operation){
        return MATH.contains(operation);
    }

    public static float calculate(float a, float b, EType operand){
        switch (operand){
            case PLUS: return a + b;
            case MINUS: return a - b;
            case MULT: return a * b;
            case DIV: return a / b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }

    public static boolean doCondition(float a, float b, EType operand){
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
