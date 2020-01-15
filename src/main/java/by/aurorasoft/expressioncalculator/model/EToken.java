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
    private float value = -1;

    /**
     *  create token with operation +, -, *, > ...
     */
    public EToken(EType type) {
        this.type = type;
        this.tokenId = -1;
        this.paramName = null;
    }

    /**
     *  create token with message param
     */
    public EToken(EType type, String paramName) {
        this.type = type;
        this.tokenId = -1;
        this.paramName = paramName;
    }

    /**
     *  create token with sensor param
     */
    public EToken(EType type, long tokenId) {
        this.type = type;
        this.tokenId = tokenId;
        this.paramName = null;
    }

    /**
     *  create token with number
     */
    public EToken(EType type, float value) {
        this.type = type;
        this.value = value;
        this.paramName = null;
        this.tokenId = -1;
    }

    public EToken(EType type, String param, long tokenId, float value) {
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
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
                Float.compare(eToken.getValue(), getValue()) == 0 &&
                getType() == eToken.getType() &&
                Objects.equals(getParamName(), eToken.getParamName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getParamName(), getTokenId(), getValue());
    }
}
