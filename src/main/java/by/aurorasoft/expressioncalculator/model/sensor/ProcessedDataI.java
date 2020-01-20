package by.aurorasoft.expressioncalculator.model.sensor;

public interface ProcessedDataI extends Comparable<ProcessedDataI> {

    float getMin();
    float getMax();
    float getOffset();
    float getFactor();
    String getText();

    float calc(float value);
    int compareTo(ProcessedDataI o);
}
