package by.aurorasoft.expressioncalculator.model.sensor;

public interface RawDataI extends Comparable<RawDataI> {
    float getInValue();
    float getActualValue();
    String getText();

    int compareTo(RawDataI o);
}
