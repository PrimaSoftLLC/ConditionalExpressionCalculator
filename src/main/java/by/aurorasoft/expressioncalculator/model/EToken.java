package by.aurorasoft.expressioncalculator.model;

import java.util.Objects;

/**
 *
 */
public class EToken {
    private final EType type;
    /**
     * contains field name from Message
     */
    private final String paramName;

    /**
     * contains sensor id
     */
    private final long tokenId;

    /**
     * contains value. This value set before calculations in
     * @see by.aurorasoft.expressioncalculator.ExpressionCalculator}
     */
    private double value;

    /**
     *  create token with operation +, -, *, > ...
     */
    public static EToken operation(EType t){
        return new EToken(t, null, -1, -1);
    }

    /**
     *  create token with message param
     */
    public static EToken messageParamName(String paramName){
        return new EToken(EType.PARAM, paramName, -1, -1);
    }

    /**
     *  create token with number
     */
    public static EToken number(double v){
        return new EToken(EType.NUMB, null, -1, v);
    }

    /**
     *  create token with sensor param
     */
    public static EToken sensorId(long id){
        return new EToken(EType.TOKEN, null, id, -1);
    }

    private EToken(EType type, String param, long tokenId, double value) {
        this.type = type;
        this.tokenId = tokenId;
        this.value = value;
        this.paramName = param;
    }

    public boolean is(EType t){
        return type == t;
    }

    public EType getType() {
        return type;
    }

    public long getTokenId() {
        return tokenId;
    }

    public String getParamName(){
        return paramName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EToken{" +
                "type=" + type +
                ", paramName='" + paramName + '\'' +
                ", tokenId=" + tokenId +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EToken)) return false;
        EToken eToken = (EToken) o;
        return getTokenId() == eToken.getTokenId() &&
                Double.compare(eToken.getValue(), getValue()) == 0 &&
                getType() == eToken.getType() &&
                Objects.equals(getParamName(), eToken.getParamName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getParamName(), getTokenId(), getValue());
    }
}
