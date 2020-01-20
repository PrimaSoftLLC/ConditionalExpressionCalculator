package by.aurorasoft.expressioncalculator.model.sensor.impl;

import by.aurorasoft.expressioncalculator.model.sensor.ParameterI;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParameterImpl implements ParameterI {
    private Long id;
    private String name;
}
