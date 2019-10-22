package by.aurorasoft.expressioncalculator.model;

import by.aurorasoft.expressioncalculator.SupportedOperations;

import java.util.HashMap;
import java.util.Map;

public class ETokens {

    private final static Map<String, EToken> cache = new HashMap<>();

    static {
        cache.put("+", new EToken(EType.PLUS));
        cache.put("-", new EToken(EType.MINUS));
        cache.put("/", new EToken(EType.DIV));
        cache.put("*", new EToken(EType.MULT));
        cache.put(">", new EToken(EType.MORE));
        cache.put(">=", new EToken(EType.MORE_E));
        cache.put("<", new EToken(EType.LESS));
        cache.put("<=", new EToken(EType.LESS_E));
        cache.put("=", new EToken(EType.EQUAL));
        cache.put("!=", new EToken(EType.NOT_E));
        cache.put("(", new EToken(EType.OPEN_B));
        cache.put(")", new EToken(EType.CLOSE_B));
        cache.put("&", new EToken(EType.AND));
        cache.put("|", new EToken(EType.OR));
    }

    public static EToken create(String s) {
        try {
            EToken t = cache.get(s);
            if (t != null) {
                return t;
            }
            if (s.charAt(0) == SupportedOperations.START_SENSOR_ID_TOKEN) {
                return new EToken(EType.TOKEN, Long.parseLong(s.substring(1, s.length() - 1)));
            }
            return new EToken(EType.NUMB, Float.parseFloat(s));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Token: '%s' can't be converted", s));
        }
    }
}
