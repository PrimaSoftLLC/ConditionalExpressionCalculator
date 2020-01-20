package by.aurorasoft.expressioncalculator.model.sensor.impl;

import by.aurorasoft.expressioncalculator.model.sensor.RawDataI;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RawDataImpl implements RawDataI {

    private final float inValue;
    private final float actualValue;
    private final String text;

    public float getInValue() {
        return inValue;
    }

    public float getActualValue() {
        return actualValue;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(RawDataI o) {
        if (inValue - o.getInValue() < 0) {
            return -1;
        }
        if (inValue - o.getInValue() > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
