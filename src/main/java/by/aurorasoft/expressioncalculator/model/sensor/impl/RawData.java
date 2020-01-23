package by.aurorasoft.expressioncalculator.model.sensor.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class RawData implements Comparable<RawData> {

    private final float inValue;
    private final float actualValue;
    private final String text;

    @JsonCreator
    public RawData(@JsonProperty("inValue") float inValue, @JsonProperty("actualValue") float actualValue,
                   @JsonProperty("text") String text) {
        this.inValue = inValue;
        this.actualValue = actualValue;
        this.text = text;
    }

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
