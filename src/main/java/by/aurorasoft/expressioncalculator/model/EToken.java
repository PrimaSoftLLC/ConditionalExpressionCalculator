package by.aurorasoft.expressioncalculator.model;

import java.util.Objects;

/**
 *
 */
public class EToken {
    private final EType type;
    private long tokenId = -1;
    private float value = -1;

    public EToken(EType type) {
        this.type = type;
    }

    public EToken(EType type, long tokenId) {
        this.type = type;
        this.tokenId = tokenId;
    }

    public EToken(EType type, float value) {
        this.type = type;
        this.value = value;
    }

    public EToken(EType type, long tokenId, float value) {
        this.type = type;
        this.tokenId = tokenId;
        this.value = value;
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

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
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
                ", tokenId=" + tokenId +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EToken)) return false;
        EToken eToken = (EToken) o;
        return tokenId == eToken.tokenId &&
                Float.compare(eToken.value, value) == 0 &&
                type == eToken.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, tokenId, value);
    }
}
