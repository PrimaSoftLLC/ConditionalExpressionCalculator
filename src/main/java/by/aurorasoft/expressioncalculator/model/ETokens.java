package by.aurorasoft.expressioncalculator.model;

public class ETokens {

    public static EToken create(String s) {
        try {
            switch (s) {
                case "+":
                    return new EToken(EType.PLUS);
                case "-":
                    return new EToken(EType.MINUS);
                case "/":
                    return new EToken(EType.DIV);
                case "*":
                    return new EToken(EType.MULT);
                case ">":
                    return new EToken(EType.MORE);
                case ">=":
                    return new EToken(EType.MORE_E);
                case "<":
                    return new EToken(EType.LESS);
                case "<=":
                    return new EToken(EType.LESS_E);
                case "=":
                    return new EToken(EType.EQUAL);
                case "!=":
                    return new EToken(EType.NOT_E);
                case "(":
                    return new EToken(EType.OPEN_B);
                case ")":
                    return new EToken(EType.CLOSE_B);
                case "&":
                    return new EToken(EType.AND);
                case "|":
                    return new EToken(EType.OR);
            }
            if (s.startsWith("{")) {
                return new EToken(EType.TOKEN, Long.parseLong(s.substring(1, s.length() - 1)));
            }
            return new EToken(EType.NUMB, Float.parseFloat(s));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Token: '%s' can't be converted", s));
        }
    }
}
