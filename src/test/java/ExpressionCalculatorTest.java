import by.aurorasoft.expressioncalculator.ExpressionCalculator;
import by.aurorasoft.expressioncalculator.ReversePolishNotation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExpressionCalculatorTest {

    private final ExpressionCalculator calculator = new ExpressionCalculator();

    @Test
    public void calculateMath() {
        assertEquals(4, calculator.calculateMath(ReversePolishNotation.create("2+2", List.of())), 0);
        assertEquals(6, calculator.calculateMath(ReversePolishNotation.create("2+2*2", List.of())), 0);
        assertEquals(4, calculator.calculateMath(ReversePolishNotation.create("2+2*2-2", List.of())), 0);
        assertEquals(0.6666667, calculator.calculateMath(ReversePolishNotation.create("2/3", List.of())), 0.0001);
        assertEquals(2.6666667, calculator.calculateMath(ReversePolishNotation.create("2/3+2", List.of())), 0.0001);
        assertEquals(5, calculator.calculateMath(ReversePolishNotation.create("2/2+2*2", List.of())), 0);
        assertEquals(10, calculator.calculateMath(ReversePolishNotation.create("10+10-10", List.of())), 0);
        assertEquals(-80, calculator.calculateMath(ReversePolishNotation.create("10+10-100", List.of())), 0);
    }

    @Test
    public void calculateMathWithBrackets() {
        assertEquals(5, calculator.calculateMath(ReversePolishNotation.create("(6+10-4)/(1+1*2)+1", List.of())), 0);
        assertEquals(-10, calculator.calculateMath(ReversePolishNotation.create("10*(0-1)", List.of())), 0);
        assertEquals(7, calculator.calculateMath(ReversePolishNotation.create("(6+10-4)/(1+1*2)+1*3", List.of())), 0);
    }

    @Test
    public void calculateConditional() {
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("10>0", List.of())));
        assertFalse(calculator.calculateConditional(ReversePolishNotation.create("10>100", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100>=100", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100=100", List.of())));
        assertFalse(calculator.calculateConditional(ReversePolishNotation.create("100!=100", List.of())));
        assertFalse(calculator.calculateConditional(ReversePolishNotation.create("100<90", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100<120", List.of())));
        assertFalse(calculator.calculateConditional(ReversePolishNotation.create("100+100<120", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100+100<120*2", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100+100!=120*2/2", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100+100=100*2", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("100>0&10=10", List.of())));
        assertFalse(calculator.calculateConditional(ReversePolishNotation.create("100>0&10=11", List.of())));
        assertFalse(calculator.calculateConditional(ReversePolishNotation.create("0>0&0=0", List.of())));
        assertTrue(calculator.calculateConditional(ReversePolishNotation.create("0<=0", List.of())));
    }

    @Test
    public void test() {
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("0>0&0=0", List.of())));
    }

    @Test
    public void calculateLogic() {
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10>0&100<120", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10+1>0&100+1<120", List.of())));
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("10+1>0&100+100<120", List.of())));
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("10>0&&100<1", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10>0|100<120", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10>0|100<120/2", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10>0|100<120|150>120", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10>50|100<120|150>120", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10>50|200<120|150>120", List.of())));
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("10<50|200<120|200<120", List.of())));
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("10<0|100>120|100>120", List.of())));
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("10<0|(100>120&100>120)", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10<100|(100>120&100<120)", List.of())));
        assertEquals(true, calculator.calculateConditional(ReversePolishNotation.create("10*2+2<100|(100>120&100<120)", List.of())));
    }

    @Test
    public void calculateLogic2() {
        assertEquals(false, calculator.calculateConditional(ReversePolishNotation.create("50<10|200<120|220<140", List.of())));
    }
}
