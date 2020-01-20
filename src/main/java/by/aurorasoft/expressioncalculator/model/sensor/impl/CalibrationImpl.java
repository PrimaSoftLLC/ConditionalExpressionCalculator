package by.aurorasoft.expressioncalculator.model.sensor.impl;

import by.aurorasoft.expressioncalculator.model.sensor.CalibrationI;
import by.aurorasoft.expressioncalculator.model.sensor.ProcessedDataI;
import by.aurorasoft.expressioncalculator.model.sensor.RawDataI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

@Getter
public class CalibrationImpl implements CalibrationI {
    private final TreeSet<ProcessedDataI> processedDataList;
    private final TreeSet<RawDataI> rawDataList;

    public CalibrationImpl() {
        processedDataList = new TreeSet<>();
        rawDataList = new TreeSet<>();
    }

    public CalibrationImpl(List<RawDataI> rawData) {
        if (rawData == null) {
            this.rawDataList = new TreeSet<>();
            this.processedDataList = new TreeSet<>();
        } else {
            this.rawDataList = new TreeSet<>(rawData);
            this.processedDataList = calculateProcessedData();
        }
    }

    public float calculateCalibrationValue(float value) {
        if (processedDataList.size() == 0) {
            return value;
        }
        return processedDataList.floor(new ProcessedDataImpl(value, value)).calc(value);
    }

    private TreeSet<ProcessedDataI> calculateProcessedData() {

        TreeSet<ProcessedDataI> result = new TreeSet<>();
        if (rawDataList == null) {
            return result;
        }
        if (rawDataList.size() < 2) {
            return result;
        }
        Iterator<RawDataI> iterator = rawDataList.iterator();
        RawDataI prev = iterator.next();
        while (iterator.hasNext()) {
            RawDataI cur = iterator.next();
            result.add(calculateProccesedData(prev, cur));
            prev = cur;
        }
        ProcessedDataI first = result.first();
        result.add(new ProcessedDataImpl(Float.MAX_VALUE * -1, first.getMin(), first.getOffset(),
                first.getFactor(), first.getText()));
        ProcessedDataI last = result.last();
        result.add(new ProcessedDataImpl(last.getMax(), Float.MAX_VALUE, last.getOffset(),
                last.getFactor(), last.getText()));
        return result;
    }

    private ProcessedDataI calculateProccesedData(RawDataI current, RawDataI next) {
        final float x1 = current.getInValue();
        final float x2 = next.getInValue();

        final float y1 = current.getActualValue();
        final float y2 = next.getActualValue();

        final MetaValues metaValues = createMetaValues(y1, y2, x1, x2, current);

        return new ProcessedDataImpl(x1, x2, metaValues.offset, metaValues.factor, metaValues.text);
    }

    private MetaValues createMetaValues(float y1, float y2, float x1, float x2, RawDataI valueDTO) {
        return new MetaValues(calculateFactor(y1, y2, x1, x2),
                calculateOffset(y1, y2, x1, x2), valueDTO.getText());
    }

    private float calculateFactor(float y1, float y2, float x1, float x2) {
        return (y1 - y2) / (x1 - x2);
    }

    private float calculateOffset(float y1, float y2, float x1, float x2) {
        return y2 - (x2 * (y1 - y2) / (x1 - x2));
    }

    @AllArgsConstructor
    private static class MetaValues {
        private float factor;
        private float offset;
        private String text;
    }
}
