package by.aurorasoft.expressioncalculator.model.sensor;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration;

import java.util.List;
import java.util.Map;

public interface SensorI {

    Long getId();

    String getName();

    ParameterI getParameter();

    Calibration getCalibration();

    String getFormulaRaw();

    String getConditionRaw();

    List<EToken> getFormula();

    List<EToken> getCondition();

    void setFormula(List<EToken> tokens);

    void setCondition(List<EToken> tokens);

    Map<String, String> getOptions();
}
