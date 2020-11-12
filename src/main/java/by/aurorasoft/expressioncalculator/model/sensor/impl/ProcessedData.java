package by.aurorasoft.expressioncalculator.model.sensor.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessedData implements Comparable<ProcessedData>, Serializable {
    private final double min;
    private final double max;
    private final double offset;
    private final double factor;
    private final String text;

    public ProcessedData(double min, double max) {
        this.min = min;
        this.max = max;
        this.offset = 0;
        this.factor = 1;
        this.text = "";
    }

    @JsonCreator
    public ProcessedData(@JsonProperty("min") double min, @JsonProperty("max") double max,
                         @JsonProperty("offset") double offset, @JsonProperty("factor") double factor,
                         @JsonProperty("text") String text) {
        this.min = min;
        this.max = max;
        this.offset = offset;
        this.factor = factor;
        this.text = text;
    }

    public double calc(double value) {
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
