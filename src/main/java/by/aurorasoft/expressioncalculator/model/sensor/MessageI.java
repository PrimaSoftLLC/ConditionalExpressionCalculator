package by.aurorasoft.expressioncalculator.model.sensor;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public interface MessageI {

    Long getId();

    /** UTC */
    Instant getDatetime();

    float getLatitude();

    float getLongitude();

    /**
     * meters
     */
    int getAltitude();

    /**
     * km/h
     */
    int getSpeed();

    int getAmountSatellite();
    int getCourse();

    /**
     * telemetry data
     * {paramId}:{type}:{value} comma separated
     */
    String getParams();

    /**
     * should get value from field for speed, latitude, longitude, amountSatellite, course, altitude
     * @return value or -999999 if was exception
     */
    default double getFieldValue(String fieldName) {
        try {
            return Double.parseDouble(this.getClass().getMethod("get" + StringUtils.capitalize(fieldName)).invoke(this).toString());
        } catch (Exception e){
            return -999999;
        }
    }
}
