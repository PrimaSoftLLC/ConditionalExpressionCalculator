import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SupportedOperations {

    public static final String MULTIPLY = "*";
    public static final String DIVISION = "/";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MORE = ">";
    public static final String MORE_OR_EQUALS = ">=";
    public static final String LESS = "<";
    public static final String LESS_OR_EQUALS = "<=";
    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "!=";
    public static final String OR = "|";
    public static final String AND = "&";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";

    public static final char START_SENSOR_ID_TOKEN = '{';
    public static final char END_SENSOR_ID_TOKEN = '}';

    public static Set<String> MATH = Set.of(MULTIPLY, DIVISION, PLUS, MINUS);

    public static Set<String> CONDITIONALS = Set.of(MORE, MORE_OR_EQUALS, LESS, LESS_OR_EQUALS, EQUALS, NOT_EQUALS);

    public static Set<String> LOGIC = Set.of(OR, AND);

    public static final Set<Character> UNO_OPERATIONS = Set.of(PLUS.charAt(0), MINUS.charAt(0), MULTIPLY.charAt(0),
            DIVISION.charAt(0), OPEN_BRACKET.charAt(0), CLOSE_BRACKET.charAt(0), OR.charAt(0), AND.charAt(0));

    public static Map<String, Integer> PRIORITY = new HashMap<>();
    
    static {
        PRIORITY.put(MULTIPLY, 4);
        PRIORITY.put(DIVISION, 4);
        PRIORITY.put(PLUS, 3);
        PRIORITY.put(MINUS, 3);
        PRIORITY.put(MORE, 2);
        PRIORITY.put(MORE_OR_EQUALS, 2);
        PRIORITY.put(LESS, 2);
        PRIORITY.put(LESS_OR_EQUALS, 2);
        PRIORITY.put(EQUALS, 2);
        PRIORITY.put(NOT_EQUALS, 2);
        PRIORITY.put(AND, 1);
        PRIORITY.put(OR, 1);
        PRIORITY.put(OPEN_BRACKET, 0);
        PRIORITY.put(CLOSE_BRACKET, 0);
    }

    public static Set getAll(){
        return PRIORITY.keySet();
    }

    public static boolean contains(String operation) {
        return PRIORITY.containsKey(operation);
    }

    public static boolean isCondition(String operation){
        return CONDITIONALS.contains(operation);
    }

    public static boolean isLogic(String operation){
        return LOGIC.contains(operation);
    }

    public static boolean isMath(String operation){
        return MATH.contains(operation);
    }

    public static float calculate(float a, float b, String operand){
        switch (operand){
            case PLUS: return a + b;
            case MINUS: return a - b;
            case MULTIPLY: return a * b;
            case DIVISION: return a / b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }

    public static boolean doCondition(float a, float b, String operand){
        switch (operand){
            case MORE: return a > b;
            case MORE_OR_EQUALS: return a >= b;
            case LESS: return a < b;
            case LESS_OR_EQUALS: return a <= b;
            case EQUALS: return a == b;
            case NOT_EQUALS: return a != b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }

    public static boolean doLogic(boolean a, boolean b, String operand){
        switch (operand){
            case OR: return a || b;
            case AND: return a && b;
            default: throw new UnsupportedOperationException(String.format("operand: %s is not supported", operand));
        }
    }
}
