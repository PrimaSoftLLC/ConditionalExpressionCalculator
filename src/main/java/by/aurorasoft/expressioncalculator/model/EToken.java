package by.aurorasoft.expressioncalculator.model;

import java.util.Objects;

public class EToken {
    private final EType type;

    /**
     * contains property name from Message
     *
     */
    private final String propertyName;

    /**
     * contains parameter name from params
     *
     */
    private final String parameterName;

    /**
     * contains sensor id
     */
    private final long sensorId;

    /**
     * Literal or legacy assigned value. SensorCalculator resolves dynamic values
     * without modifying this field. Do not mutate shared tokens during calculation.
     * @see by.aurorasoft.expressioncalculator.ExpressionCalculator}
     */
    private double value;

    /**
     *  create token with operation +, -, *, > ...
     */
    public static EToken operation(EType t){
        return new EToken(t, null, null, -1, -1);
    }

    /**
     *  create token with message property
     */
    public static EToken property(String propertyName){
        return new EToken(EType.PROPERTY, propertyName, null, -1, -1);
    }

    /**
     *  create token with message property
     */
    public static EToken parameter(String parameterName){
        return new EToken(EType.PARAMETER, null, parameterName, -1, -1);
    }

    /**
     *  create token with number
     */
    public static EToken number(double v){
        return new EToken(EType.NUMB, null, null, -1, v);
    }

    /**
     *  create token with sensor param
     */
    public static EToken sensorId(long id){
        return new EToken(EType.SENSOR_ID, null, null, id, -1);
    }

    private EToken(EType type, String propertyName, String parameterName, long sensorId, double value) {
        this.type = type;
        this.sensorId = sensorId;
        this.value = value;
        this.propertyName = propertyName;
        this.parameterName = parameterName;
    }

    public boolean is(EType t){
        return type == t;
    }

    public EType getType() {
        return type;
    }

    public long getSensorId() {
        return sensorId;
    }

    public String getPropertyName(){
        return propertyName;
    }

    public String getParameterName() {
        return parameterName;
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
                ", propertyName='" + propertyName + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", tokenId=" + sensorId +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EToken)) return false;
        EToken eToken = (EToken) o;
        return getSensorId() == eToken.getSensorId() &&
                Double.compare(eToken.getValue(), getValue()) == 0 &&
                getType() == eToken.getType() &&
                Objects.equals(getPropertyName(), eToken.getPropertyName()) &&
                Objects.equals(parameterName, eToken.parameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getPropertyName(), parameterName, getSensorId(), getValue());
    }
}
