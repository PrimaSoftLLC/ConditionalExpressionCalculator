package by.aurorasoft.expressioncalculator.model.sensor.impl;

import by.aurorasoft.expressioncalculator.model.sensor.MessageI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class MessageImpl implements MessageI {

    private final Long id;
    private final Instant datetime;
    private final float latitude;
    private final float longitude;
    private final int altitude;
    private final int speed;
    private final int amountSatellite;
    private final int course;
    private final double gpsOdometer;
    private final int gsmStrength;
    private final int battery;
    private final float onboardVoltage;
    private final String params;

    @Override
    public double getPropertyValue(String propertyName) {
        try {
            Field field = this.getClass().getDeclaredField(propertyName);
            return Double.parseDouble(field.get(this).toString());
        } catch (Exception e) {
            return -999999;
        }
    }
}
