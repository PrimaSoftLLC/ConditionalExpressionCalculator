import by.aurorasoft.expressioncalculator.ExpressionChecker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    public void check4() {
        checker.checkLogic("{101}>{111}");
    }

    @Test
    public void check5() {
        checker.checkLogic("({101}>{111})|({234}+{456})");
    }

    @Test
    public void checkMathSensorsIsString() {
        checker.checkLogic("({датчик1}>{датчик2})|({датчик3}+{датчик4})");
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


    @Test
    public void checkLogicNull() {
        checker.checkLogic(null);
    }
}
