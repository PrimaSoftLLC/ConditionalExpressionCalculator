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
        checker.check("2+2");
    }

    @Test
    public void check2() {
        checker.check("2+2/2");
    }

    @Test
    public void check3() {
        checker.check("{101}+2/{111}");
    }

    @Test
    public void check4() {
        checker.check("{101}>{111}");
    }

    @Test
    public void check5() {
        checker.check("({101}>{111})|({234}+{456})");
    }

    @Test
    public void check6() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Illegal logic expression - result is not boolean. Wrong use operand '+'");
        checker.check("({101}>{111})+({234}+{456})");
    }

    @Test
    public void check7() {
        checker.check("{101}>{111}+3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check8() {
        checker.check("{101}+({111}<3)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check9() {
        checker.check("({101}+({111}|3))");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check10() {
        checker.check("({101}+({111}|3))|{109}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check11() {
        checker.check("(10+10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check12() {
        checker.check("10+10)");
    }


}
