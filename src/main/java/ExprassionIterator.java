
/**
 * iterate by Math or Conditional or Logic expression
 */
public class ExprassionIterator {

    private final String raw;
    private final int length;
    private int index = 0;

    public ExprassionIterator(String raw) {
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
    public String next() {
        String r = new String();
        //read digit
        if (Character.isDigit(raw.charAt(index))) {
            while (index <= length - 1 && Character.isDigit(raw.charAt(index))) {
                r += raw.charAt(index++);
            }
            return r;
        }
        //read sensor Id for example {113}
        if (raw.charAt(index) == SupportedOperations.START_SENSOR_ID_TOKEN) {
            while (index <= length - 1 && raw.charAt(index) != SupportedOperations.END_SENSOR_ID_TOKEN) {
                r += raw.charAt(index++);
            }
            r += raw.charAt(index++);
            return r;
        }

        if (SupportedOperations.UNO_OPERATIONS.contains(raw.charAt(index))) {
            r += raw.charAt(index++);
            return r;
        }

        while (index <= length - 1 && !Character.isDigit(raw.charAt(index))
                && raw.charAt(index) != SupportedOperations.START_SENSOR_ID_TOKEN
                && !SupportedOperations.UNO_OPERATIONS.contains(raw.charAt(index))) {
            r += raw.charAt(index++);
        }
        return r;
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
