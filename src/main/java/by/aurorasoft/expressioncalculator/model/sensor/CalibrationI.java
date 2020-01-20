package by.aurorasoft.expressioncalculator.model.sensor;

import java.util.TreeSet;

public interface CalibrationI {
    TreeSet<ProcessedDataI> getProcessedDataList();
    TreeSet<RawDataI> getRawDataList();
    float calculateCalibrationValue(float value);
}
