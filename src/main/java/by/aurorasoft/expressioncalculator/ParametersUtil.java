package by.aurorasoft.expressioncalculator;

public class ParametersUtil {

    public static double getParameterValue(String parameterName, String params) {
        try {
            if (parameterName == null) {
                return Double.NaN;
            }
            String name = parameterName + SupportedOperations.MESSAGE_PARAMS_KEY_VALUE_SEPARATOR;
            int start;
            if (params.startsWith(name)) {
                start = 0;
            } else {
                name = "," + name;
                start = params.indexOf(name);
            }
            if (start == -1) {
                return Double.NaN;
            }
            start = start + name.length();
            int end = params.indexOf(',', start);
            if (end == -1) {
                return Double.parseDouble(params.substring(start));
            } else {
                return Double.parseDouble(params.substring(start, end));
            }
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
