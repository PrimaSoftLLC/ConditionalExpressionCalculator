import by.aurorasoft.expressioncalculator.ExpressionCalculator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionCalculatorTest {

    private final ExpressionCalculator calculator = new ExpressionCalculator();

    @Test
    public void calculateMath() {
        assertEquals(4, calculator.calculateMath("2+2"), 0);
        assertEquals(6, calculator.calculateMath("2+2*2"), 0);
        assertEquals(4, calculator.calculateMath("2+2*2-2"), 0);
        assertEquals(0.6666667, calculator.calculateMath("2/3"), 0.0001);
        assertEquals(2.6666667, calculator.calculateMath("2/3+2"), 0.0001);
        assertEquals(5, calculator.calculateMath("2/2+2*2"), 0);
        assertEquals(10, calculator.calculateMath("10+10-10"), 0);
        assertEquals(-80, calculator.calculateMath("10+10-100"), 0);
    }

    @Test
    public void calculateMathWithBrackets() {
        assertEquals(5, calculator.calculateMath("(6+10-4)/(1+1*2)+1"), 0);
        assertEquals(-10, calculator.calculateMath("10*(0-1)"), 0);
        assertEquals(7, calculator.calculateMath("(6+10-4)/(1+1*2)+1*3"), 0);
    }

    @Test
    public void calculateConditional() {
        assertEquals(true, calculator.calculateConditional("10>0"));
        assertEquals(false, calculator.calculateConditional("10>100"));
        assertEquals(true, calculator.calculateConditional("100>=100"));
        assertEquals(true, calculator.calculateConditional("100=100"));
        assertEquals(false, calculator.calculateConditional("100!=100"));
        assertEquals(false, calculator.calculateConditional("100<90"));
        assertEquals(true, calculator.calculateConditional("100<120"));
        assertEquals(false, calculator.calculateConditional("100+100<120"));
        assertEquals(true, calculator.calculateConditional("100+100<120*2"));
        assertEquals(true, calculator.calculateConditional("100+100!=120*2/2"));
        assertEquals(true, calculator.calculateConditional("100+100=100*2"));
        assertEquals(true, calculator.calculateConditional("100>0&10=10"));
        assertEquals(false, calculator.calculateConditional("100>0&10=11"));
        assertEquals(false, calculator.calculateConditional("0>0&0=0"));
        assertEquals(true, calculator.calculateConditional("0<=0"));
    }

    @Test
    public void test(){
        assertEquals(false, calculator.calculateConditional("0>0&0=0"));
    }

    @Test
    public void calculateLogic() {
        assertEquals(true, calculator.calculateConditional("10>0&100<120"));
        assertEquals(true, calculator.calculateConditional("10+1>0&100+1<120"));
        assertEquals(false, calculator.calculateConditional("10+1>0&100+100<120"));
        assertEquals(false, calculator.calculateConditional("10>0&&100<1"));
        assertEquals(true, calculator.calculateConditional("10>0|100<120"));
        assertEquals(true, calculator.calculateConditional("10>0|100<120/2"));
        assertEquals(true, calculator.calculateConditional("10>0|100<120|150>120"));
        assertEquals(true, calculator.calculateConditional("10>50|100<120|150>120"));
        assertEquals(true, calculator.calculateConditional("10>50|200<120|150>120"));
        assertEquals(false, calculator.calculateConditional("10<50|200<120|200<120"));
        assertEquals(false, calculator.calculateConditional("10<0|100>120|100>120"));
        assertEquals(false, calculator.calculateConditional("10<0|(100>120&100>120)"));
        assertEquals(true, calculator.calculateConditional("10<100|(100>120&100<120)"));
        assertEquals(true, calculator.calculateConditional("10*2+2<100|(100>120&100<120)"));
    }

    @Test
    public void calculateLogic2() {
        assertEquals(false, calculator.calculateConditional("50<10|200<120|220<140"));
    }
}
