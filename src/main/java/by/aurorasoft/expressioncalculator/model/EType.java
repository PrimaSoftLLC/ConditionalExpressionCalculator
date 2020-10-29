package by.aurorasoft.expressioncalculator.model;
/**
 * present type of token
 * {@link by.aurorasoft.expressioncalculator.SupportedOperations}
 * MATH - matematical operations (+, -, *, /)
 * COND - conditional operations (>, >=, <, <=, =, !=)
 * LOGIC - logical operations (&, |)
 * NUMB - number int or double
 * TOKEN - value in the braces {} ({sensor one}, {rpm sensor} ...)
 * OPEN_B - open bracket "("
 * CLOSE_B - close bracket ")"
 */
public enum EType {
    PLUS("+"), MINUS("-"), MULT("*"), DIV("/"),
    MORE(">"), MORE_E(">="), LESS("<="), LESS_E("<="), EQUAL("="), NOT_E("!="),
    OR("|"), AND("&"),
    NUMB("numb"),
    SENSOR_ID("token"),
    PROPERTY("property"),
    PARAMETER("parameter"),
    OPEN_B("("), CLOSE_B(")");

    public final String detail;

    EType(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "EType{" + detail + '}';
    }
}
