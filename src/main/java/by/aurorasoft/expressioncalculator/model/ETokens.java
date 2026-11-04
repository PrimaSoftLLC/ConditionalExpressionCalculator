package by.aurorasoft.expressioncalculator.model;

import by.aurorasoft.expressioncalculator.SupportedOperations;

import java.util.HashMap;
import java.util.Map;

public class ETokens {

    private final static Map<String, EToken> cache = new HashMap<>();
    private final static String START_PROPERTY_TOKEN = "" + SupportedOperations.START_SENSOR_ID_TOKEN + SupportedOperations.MESSAGE_PROPERTY_TOKEN;
    private final static String START_PARAMETER_TOKEN = "" + SupportedOperations.START_SENSOR_ID_TOKEN + SupportedOperations.MESSAGE_PARAMETER_TOKEN;

    static {
        cache.put("+", EToken.operation(EType.PLUS));
        cache.put("-", EToken.operation(EType.MINUS));
        cache.put("/", EToken.operation(EType.DIV));
        cache.put("*", EToken.operation(EType.MULT));
        cache.put(">", EToken.operation(EType.MORE));
        cache.put(">=", EToken.operation(EType.MORE_E));
        cache.put("<", EToken.operation(EType.LESS));
        cache.put("<=", EToken.operation(EType.LESS_E));
        cache.put("=", EToken.operation(EType.EQUAL));
        cache.put("!=", EToken.operation(EType.NOT_E));
        cache.put("(", EToken.operation(EType.OPEN_B));
        cache.put(")", EToken.operation(EType.CLOSE_B));
        cache.put("&", EToken.operation(EType.AND));
        cache.put("|", EToken.operation(EType.OR));
        cache.put("bit", EToken.operation(EType.BIT));
        cache.put(",", EToken.operation(EType.COMMA));
    }

    public static EToken create(String s) {
        try {
            EToken t = cache.get(s);
            if (t != null) {
                return t;
            }
            if (s.startsWith(START_PROPERTY_TOKEN)) {
                return EToken.property(s.substring(2, s.length() - 1));
            }
            if (s.startsWith(START_PARAMETER_TOKEN)) {
                return EToken.parameter(s.substring(2, s.length() - 1));
            }
            if (s.charAt(0) == SupportedOperations.START_SENSOR_ID_TOKEN) {
                return EToken.sensorId(Long.parseLong(s.substring(1, s.length() - 1)));
            }
            return EToken.number(Double.parseDouble(s));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Token: '%s' can't be converted", s));
        }
    }
}
