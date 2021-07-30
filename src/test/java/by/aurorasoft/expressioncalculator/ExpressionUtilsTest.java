package by.aurorasoft.expressioncalculator;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ExpressionUtilsTest {

    @Test
    public void getAllParamNamesFromExperession() {

        assertEquals(Set.of(), ExpressionUtils.getAllParamNamesFromExpression(null));
        assertEquals(Set.of(), ExpressionUtils.getAllParamNamesFromExpression(""));
        assertEquals(Set.of(), ExpressionUtils.getAllParamNamesFromExpression("123"));
        assertEquals(Set.of(), ExpressionUtils.getAllParamNamesFromExpression("{}"));
        assertEquals(Set.of(), ExpressionUtils.getAllParamNamesFromExpression("aaa"));
        assertEquals(Set.of("123"), ExpressionUtils.getAllParamNamesFromExpression("{@123}"));
        assertEquals(Set.of(), ExpressionUtils.getAllParamNamesFromExpression("{123}"));
        assertEquals(Set.of("123", "1"), ExpressionUtils.getAllParamNamesFromExpression("{@123}+{@1}"));
        assertEquals(Set.of("123"), ExpressionUtils.getAllParamNamesFromExpression("{@123}+{@123}"));
    }
}
