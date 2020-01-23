package by.aurorasoft.expressioncalculator.model.sensor.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

@Data
@Getter
public class Calibration {
    private final TreeSet<ProcessedData> processedDataList;
    private final TreeSet<RawData> rawDataList;

    public Calibration() {
        processedDataList = new TreeSet<>();
        rawDataList = new TreeSet<>();
    }

    public Calibration(@JsonProperty("rawDataList") List<RawData> rawData) {
        this(rawData, List.of());
    }

    @JsonCreator
    public Calibration(@JsonProperty("rawDataList") List<RawData> rawData, @JsonProperty("processedDataList") List<ProcessedData> processedDataList) {
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
        return processedDataList.floor(new ProcessedData(value, value)).calc(value);
    }

    private TreeSet<ProcessedData> calculateProcessedData() {

        TreeSet<ProcessedData> result = new TreeSet<>();
        if (rawDataList == null) {
            return result;
        }
        if (rawDataList.size() < 2) {
            return result;
        }
        Iterator<RawData> iterator = rawDataList.iterator();
        RawData prev = iterator.next();
        while (iterator.hasNext()) {
            RawData cur = iterator.next();
            result.add(calculateProccesedData(prev, cur));
            prev = cur;
        }
        ProcessedData first = result.first();
        result.add(new ProcessedData(Float.MAX_VALUE * -1, first.getMin(), first.getOffset(),
                first.getFactor(), first.getText()));
        ProcessedData last = result.last();
        result.add(new ProcessedData(last.getMax(), Float.MAX_VALUE, last.getOffset(),
                last.getFactor(), last.getText()));
        return result;
    }

    private ProcessedData calculateProccesedData(RawData current, RawData next) {
        final float x1 = current.getInValue();
        final float x2 = next.getInValue();

        final float y1 = current.getActualValue();
        final float y2 = next.getActualValue();

        final MetaValues metaValues = createMetaValues(y1, y2, x1, x2, current);

        return new ProcessedData(x1, x2, metaValues.offset, metaValues.factor, metaValues.text);
    }

    private MetaValues createMetaValues(float y1, float y2, float x1, float x2, RawData valueDTO) {
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
