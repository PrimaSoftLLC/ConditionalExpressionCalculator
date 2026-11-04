package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.ETokens;
import by.aurorasoft.expressioncalculator.model.sensor.SensorI;

import java.util.Collection;

/**
 * iterate by Math or Conditional or Logic expression
 */
public class ExpressionIterator {

    private final String raw;
    private final Collection<? extends SensorI> rawTokens;
    private final int length;
    private int index = 0;

    public ExpressionIterator(String raw, Collection<? extends SensorI> rawTokens) {
        if (raw == null) {
            this.raw = "";
        } else {
            this.raw = raw;
        }
        this.length = this.raw.length();
        if (rawTokens == null) {
            throw new IllegalArgumentException("RawToken list can't be null");
        }
        this.rawTokens = rawTokens;
    }

    /**
     * Iterate by {@link #raw} while {@link #index} < {@link #length}
     *
     * @return next operand from {@link #raw}
     */
    public EToken nextEToken() {
        return ETokens.create(nextString());
    }

    private String nextString() {

        StringBuilder r = new StringBuilder();
        //read digit
        if (Character.isDigit(raw.charAt(index))) {
            while (index <= length - 1 && (Character.isDigit(raw.charAt(index)) || raw.charAt(index) == '.')) {
                r.append(raw.charAt(index++));
            }
            return r.toString();
        }
        //read sensor Id for example {113}
        if (raw.charAt(index) == SupportedOperations.START_SENSOR_ID_TOKEN) {
            index++; // skip '{'
            while (index <= length - 1 && raw.charAt(index) != SupportedOperations.END_SENSOR_ID_TOKEN) {
                r.append(raw.charAt(index++));
            }
            index++; // skip '}'
            if (r.toString().equalsIgnoreCase("x")) {
                return "{0}";
            } else {
                return getIdByName(r.toString());
            }
        }

        // A comparison may be followed by a function name, not only a digit or reference.
        if ("<>!=".indexOf(raw.charAt(index)) >= 0) {
            r.append(raw.charAt(index++));
            if (index < length && raw.charAt(index) == '=') {
                r.append(raw.charAt(index++));
            }
            return r.toString();
        }

        if (SupportedOperations.UNO_OPERATIONS.contains(raw.charAt(index))) {
            r.append(raw.charAt(index++));
            return r.toString();
        }

        while (index <= length - 1 && !Character.isDigit(raw.charAt(index))
                && raw.charAt(index) != SupportedOperations.START_SENSOR_ID_TOKEN
                && !SupportedOperations.UNO_OPERATIONS.contains(raw.charAt(index))) {
            r.append(raw.charAt(index++));
        }
        return r.toString();
    }

    /**
     * @return false if  {@link #raw} hasn't next operand
     */
    public boolean hasNext() {
        return index < length;
    }

    public int getIndex() {
        return index;
    }

    private String getIdByName(String name) {
        StringBuilder builder = new StringBuilder("" + SupportedOperations.START_SENSOR_ID_TOKEN);
        if (name.charAt(0) == SupportedOperations.MESSAGE_PROPERTY_TOKEN) {
            if (!SupportedOperations.AVAILABLE_PARAMS.contains(name.substring(1))) {
                throw new IllegalArgumentException(String.format("param with name: %s is not available", name));
            }
            return builder.append(name).append(SupportedOperations.END_SENSOR_ID_TOKEN).toString();
        }
        if (name.charAt(0) == SupportedOperations.MESSAGE_PARAMETER_TOKEN) {
            return builder.append(name).append(SupportedOperations.END_SENSOR_ID_TOKEN).toString();
        }
        for (SensorI rawToken : rawTokens) {
            if (rawToken.getName().equalsIgnoreCase(name)) {
                return builder.append(rawToken.getId()).append(SupportedOperations.END_SENSOR_ID_TOKEN).toString();
            }
        }
        throw new IllegalArgumentException(String.format("token with name: %s not found", name));
    }
}
