package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.ETokens;

/**
 * iterate by Math or Conditional or Logic expression
 */
public class ExpressionIterator {

    private final String raw;
    private final int length;
    private int index = 0;

    public ExpressionIterator(String raw) {
        if (raw == null) {
            this.raw = "";
        } else {
            this.raw = raw.replaceAll(" ", "");
        }
        this.length = this.raw.length();
    }

    /**
     * Iterate by {@link #raw} while {@link #index} < {@link #length}
     * @return next operand from {@link #raw}
     */
    public EToken next() {
        StringBuilder r = new StringBuilder();
        //read digit
        if (Character.isDigit(raw.charAt(index))) {
            while (index <= length - 1 && (Character.isDigit(raw.charAt(index)) || raw.charAt(index) == '.')) {
                r.append(raw.charAt(index++));
            }
            return ETokens.create(r.toString());
        }
        //read sensor Id for example {113}
        if (raw.charAt(index) == SupportedOperations.START_SENSOR_ID_TOKEN) {
            while (index <= length - 1 && raw.charAt(index) != SupportedOperations.END_SENSOR_ID_TOKEN) {
                r.append(raw.charAt(index++));
            }
            r.append(raw.charAt(index++));
            return ETokens.create(r.toString());
        }

        if (SupportedOperations.UNO_OPERATIONS.contains(raw.charAt(index))) {
            r.append(raw.charAt(index++));
            return ETokens.create(r.toString());
        }

        while (index <= length - 1 && !Character.isDigit(raw.charAt(index))
                && raw.charAt(index) != SupportedOperations.START_SENSOR_ID_TOKEN
                && !SupportedOperations.UNO_OPERATIONS.contains(raw.charAt(index))) {
            r.append(raw.charAt(index++));
        }
        return ETokens.create(r.toString());
    }

    /**
     * @return true if  {@link #raw} has next operand
     * @return false if  {@link #raw} hasn't next operand
     */
    public boolean hasNext() {
        return index < length;
    }

    public int getIndex(){
        return index;
    }
}
