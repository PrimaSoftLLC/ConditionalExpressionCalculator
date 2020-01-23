package by.aurorasoft.expressioncalculator.model.sensor.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class CalibrationTest {
    @Test
    public void deserialize() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Calibration calibration = om.readValue("{\"processedDataList\":[],\"rawDataList\":[{\"inValue\":100.0,\"actualValue\":200.0,\"text\":\"\"},{\"inValue\":200.0,\"actualValue\":300.0,\"text\":\"\"}]}}", Calibration.class);
        System.out.println(calibration);
        System.out.println("rawDataList " + calibration.getRawDataList().size());
        System.out.println("processedDataList " + calibration.getProcessedDataList().size());
    }
}
