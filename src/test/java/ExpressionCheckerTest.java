import by.aurorasoft.expressioncalculator.ExpressionChecker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ExpressionCheckerTest {

    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();
    private final ExpressionChecker checker = new ExpressionChecker();

    @Test
    public void check() {
        checker.checkMath("2+2");
    }

    @Test
    public void checkMathNull() {
        checker.checkMath(null);
    }

    @Test
    public void check2() {
        checker.checkMath("2+2/2");
    }

    @Test
    public void check3() {
        checker.checkMath("{101}+2/{111}");
    }

    @Test
    public void checkMath4() {
        checker.checkMath("{sensor_name}+2");
    }

    @Test
    public void checkMath5() {
        String actual = checker.checkMath("{sensor name}");
        assertEquals("{sensor name}", actual);
    }

    @Test
    public void checkMath6() {
        String actual = checker.checkMath("{#speed}");
        assertEquals("{#speed}", actual);
    }

    @Test
    public void check4() {
        checker.checkLogic("{101}>{111}");
    }

    @Test
    public void check5() {
        checker.checkLogic("({101}>{111})|({234}>{456})");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_logic() {
        checker.checkLogic("0<{датчик}<1500");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_logic23() {
        checker.checkLogic("0<{датчик}<1500+10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_logic3() {
        checker.checkLogic("<{датчик}+3");
    }

    @Test
    public void check_logic2() {
        checker.checkLogic("0<{датчик}&{датчик}>1500");
    }

    @Test
    public void checkMathSensorsIsString() {
        checker.checkLogic("({датчик1}>{датчик2})|({датчик3}>{датчик4})");
    }

    @Test
    public void checkLogicSensorsWithParameter1IsString() {
        checker.checkLogic("({@param1}>{@param2})|({@param3}<{датчик4})");
    }

    @Test
    public void checkMathParameter1IsString() {
        checker.checkMath("({@param1}+{@param2})-({@param3}+{датчик4})");
    }

    @Test
    public void check6() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Illegal logic expression - result is not boolean. Wrong use operand '+'");
        checker.checkLogic("({101}>{111})+({234}+{456})");
    }

    @Test
    public void check7() {
        checker.checkLogic("{101}>{111}+3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check8() {
        checker.checkLogic("{101}+({111}<3)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check9() {
        checker.checkLogic("({101}+({111}|3))");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check10() {
        checker.checkLogic("({101}+({111}|3))|{109}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check11() {
        checker.checkLogic("(10+10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check12() {
        checker.checkLogic("10+10)");
    }


    @Test(expected = IllegalArgumentException.class)
    public void check13() {
        checker.checkLogic("{10+10)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check14() {
        checker.checkLogic("(10}+10)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check15() {
        checker.checkLogic("{#unknown}+10)");
    }

    @Test
    public void checkSpaces() {
        assertEquals("{s}+10", checker.checkMath("{s}   + 10"));
        assertEquals("{s}+10", checker.checkMath(" {s}   + 10 "));
        assertEquals("{s}+10", checker.checkMath("  {s}   + 10  "));
        assertEquals("{sensor one}+10+3+{sensor one rpm }", checker.checkMath("{sensor one}   + 10 + 3 + {sensor one rpm }"));

        assertEquals("({s}+10)>345", checker.checkLogic("({s}   + 10) > 345"));
        assertEquals("({s}+10)<456", checker.checkLogic(" ( {s}   + 10 )    < 456 "));
        assertEquals("({s}+10)>=45", checker.checkLogic("  ({s}   + 10 )  >= 45 "));
        assertEquals("({sensor one}+10+3+{sensor one rpm })!=5", checker.checkLogic("( {sensor one}   + 10 + 3 + {sensor one rpm } ) !=  5 "));
    }


    @Test
    public void checkLogicNull() {
        checker.checkLogic(null);
    }
}
