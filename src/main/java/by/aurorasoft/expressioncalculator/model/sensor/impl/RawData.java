package by.aurorasoft.expressioncalculator.model.sensor.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RawData implements Comparable<RawData>, Serializable {

    private final double inValue;
    private final double actualValue;
    private final String text;

    @JsonCreator
    public RawData(@JsonProperty("inValue") double inValue, @JsonProperty("actualValue") double actualValue,
                   @JsonProperty("text") String text) {
        this.inValue = inValue;
        this.actualValue = actualValue;
        this.text = text;
    }

    public double getInValue() {
        return inValue;
    }

    public double getActualValue() {
        return actualValue;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(RawData o) {
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
