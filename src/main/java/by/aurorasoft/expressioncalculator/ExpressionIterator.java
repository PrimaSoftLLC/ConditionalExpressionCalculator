package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.ETokens;
import by.aurorasoft.expressioncalculator.model.RawToken;

import java.util.List;

/**
 * iterate by Math or Conditional or Logic expression
 */
public class ExpressionIterator {

    private final String raw;
    private final List<RawToken> rawTokens;
    private final int length;
    private int index = 0;

    public ExpressionIterator(String raw, List<RawToken> rawTokens) {
        if (raw == null) {
            this.raw = "";
        } else {
            this.raw = raw;
        }
        this.length = this.raw.length();
        if (rawTokens == null) {
            throw new IllegalArgumentException(String.format("RawToken list can't be null"));
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

    public String nextString() {

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
        StringBuilder builder = new StringBuilder("{");
        for (RawToken rawToken : rawTokens) {
            if (rawToken.getName().equalsIgnoreCase(name)) {
                return builder.append(rawToken.getId()).append("}").toString();
            }
        }
        throw new IllegalArgumentException(String.format("token with name: %s not found", name));
    }
}
