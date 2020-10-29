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
    default double getPropertyValue(String propertyName) {
        try {
            return Double.parseDouble(this.getClass().getMethod("get" + StringUtils.capitalize(propertyName)).invoke(this).toString());
        } catch (Exception e){
            return -999999;
        }
    }

    default double getParameterValue(String parameterName) {
        try {
            if (parameterName == null) {
                return 0;
            }
            String name = parameterName + ":1:";
            int start = getParams().indexOf(name);
            if (start == -1) {
                return 0;
            }
            start = start + name.length();
            int end = getParams().indexOf(',', start);
            if (end == -1) {
                return Double.parseDouble(getParams().substring(start));
            } else {
                return Double.parseDouble(getParams().substring(start, end));
            }
        } catch (Exception e) {
            return 0;
        }
    }

}
