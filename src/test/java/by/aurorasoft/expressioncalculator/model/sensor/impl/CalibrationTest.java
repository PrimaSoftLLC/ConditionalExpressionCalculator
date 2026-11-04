package by.aurorasoft.expressioncalculator.model.sensor.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CalibrationTest {
    @Test
    public void deserialize() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Calibration calibration = om.readValue("{\"processedDataList\":[],\"rawDataList\":[{\"inValue\":100.0,\"actualValue\":200.0,\"text\":\"\"},{\"inValue\":200.0,\"actualValue\":300.0,\"text\":\"\"}]}}", Calibration.class);
        assertEquals(2, calibration.getRawDataList().size());
        assertEquals(3, calibration.getProcessedDataList().size());
        assertEquals(100, calibration.getRawDataList().first().getInValue(), 0);
        assertEquals(200, calibration.getRawDataList().first().getActualValue(), 0);
        assertEquals(200, calibration.getRawDataList().last().getInValue(), 0);
        assertEquals(300, calibration.getRawDataList().last().getActualValue(), 0);
    }
}
