package by.aurorasoft.expressioncalculator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExpressionUtilsTest {

    @Test
    public void getAllParamNamesFromExperession() {
        assertEquals(List.of(), ExpressionUtils.getAllParamNamesFromExpression(""));
        assertEquals(List.of(), ExpressionUtils.getAllParamNamesFromExpression("123"));
        assertEquals(List.of(), ExpressionUtils.getAllParamNamesFromExpression("{}"));
        assertEquals(List.of(), ExpressionUtils.getAllParamNamesFromExpression("aaa"));
        assertEquals(List.of("123"), ExpressionUtils.getAllParamNamesFromExpression("{@123}"));
        assertEquals(List.of(), ExpressionUtils.getAllParamNamesFromExpression("{123}"));
        assertEquals(List.of("123", "1"), ExpressionUtils.getAllParamNamesFromExpression("{@123}+{@1}"));
    }
}
