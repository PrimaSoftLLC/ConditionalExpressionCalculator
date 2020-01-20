package by.aurorasoft.expressioncalculator.model.sensor.impl;

import by.aurorasoft.expressioncalculator.model.sensor.ProcessedDataI;
import lombok.Data;

@Data
public class ProcessedDataImpl implements ProcessedDataI {
    private final float min;
    private final float max;
    private final float offset;
    private final float factor;
    private final String text;

    public ProcessedDataImpl(float min, float max) {
        this.min = min;
        this.max = max;
        this.offset = 0;
        this.factor = 1;
        this.text = "";
    }

    public ProcessedDataImpl(float min, float max, float offset, float factor, String text) {
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
    public int compareTo(ProcessedDataI o) {
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
