package by.aurorasoft.expressioncalculator.model.sensor;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.sensor.impl.Calibration;

import java.util.List;
import java.util.Map;

/**
 * A snapshot's configuration and prepared expressions must remain unchanged while
 * workers use it. Prepare and safely publish the complete snapshot before calculation.
 * Custom calculateValue implementations must also be safe for concurrent invocation.
 */
public interface SensorI {

    Long getId();

    String getName();

    Calibration getCalibration();

    String getFormulaRaw();

    String getConditionRaw();

    List<EToken> getFormula();

    List<EToken> getCondition();

    void setFormula(List<EToken> tokens);

    void setCondition(List<EToken> tokens);

    Map<String, String> getOptions();

    double calculateValue(MessageI m, Map<Long, ? extends SensorI> sensorMap);
}
