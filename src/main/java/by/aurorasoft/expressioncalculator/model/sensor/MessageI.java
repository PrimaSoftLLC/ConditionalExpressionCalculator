package by.aurorasoft.expressioncalculator.model.sensor;

import by.aurorasoft.expressioncalculator.ParametersUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public interface MessageI {

    Long getId();

    /**
     * UTC
     */
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
     *
     * @return value or -999999 if was exception
     */
    default double getPropertyValue(String propertyName) {
        try {
            return Double.parseDouble(this.getClass().getMethod("get" + StringUtils.capitalize(propertyName)).invoke(this).toString());
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    default double getParameterValue(String parameterName) {
        return ParametersUtil.getParameterValue(parameterName, getParams());
    }
}
