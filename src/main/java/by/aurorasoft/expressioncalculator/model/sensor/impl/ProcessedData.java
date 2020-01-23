package by.aurorasoft.expressioncalculator.model.sensor.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProcessedData implements Comparable<ProcessedData> {
    private final float min;
    private final float max;
    private final float offset;
    private final float factor;
    private final String text;

    public ProcessedData(float min, float max) {
        this.min = min;
        this.max = max;
        this.offset = 0;
        this.factor = 1;
        this.text = "";
    }

    @JsonCreator
    public ProcessedData(@JsonProperty("min") float min, @JsonProperty("max") float max,
                         @JsonProperty("offset") float offset, @JsonProperty("factor") float factor,
                         @JsonProperty("text") String text) {
        this.min = min;
        this.max = max;
        this.offset = offset;
        this.factor = factor;
        this.text = text;
    }

    public float calc(float value) {
        return value * factor + offset;
    }

    @Override
    public int compareTo(ProcessedData o) {
        if (o.getMin() < this.min) {
            return 1;
        }
        if (o.getMin() >= this.max) {
            return -1;
        } else {
            return 0;
        }
    }
}
