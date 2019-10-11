import by.aurorasoft.expressioncalculator.ExprassionChecker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExprassionCheckerTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    private final ExprassionChecker checker = new ExprassionChecker();

    @Test
    public void check() {
        checker.checkMath("2+2");
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
}
