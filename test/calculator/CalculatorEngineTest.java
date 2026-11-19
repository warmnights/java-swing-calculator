package calculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CalculatorEngineTest {

    private CalculatorEngine engine;

    @Before
    public void setUp() {
        engine = new CalculatorEngine();
    }

    /**
     * Presses keys in order so the tests read like a user session:
     * digits, + - * / ^ and . as themselves, = calculate, % percent,
     * s sign, r square root, q square, i reciprocal,
     * d delete, e clear entry, c clear all.
     */
    private void type(String keys) {
        for (char key : keys.toCharArray()) {
            switch (key) {
                case '+': case '-': case '*': case '/': case '^':
                    engine.chooseOperator(String.valueOf(key));
                    break;
                case '.': engine.appendDecimal(); break;
                case '=': engine.calculate(); break;
                case '%': engine.percent(); break;
                case 's': engine.toggleSign(); break;
                case 'r': engine.squareRoot(); break;
                case 'q': engine.square(); break;
                case 'i': engine.reciprocal(); break;
                case 'd': engine.deleteLast(); break;
                case 'e': engine.clearEntry(); break;
                case 'c': engine.clearAll(); break;
                default: engine.appendDigit(String.valueOf(key));
            }
        }
    }

    // evaluate()

    @Test
    public void multiplicationBindsTighterThanAddition() {
        assertEquals(7.0, CalculatorEngine.evaluate("1 + 2 * 3"), 1e-9);
        assertEquals(5.0, CalculatorEngine.evaluate("2 * 3 - 1"), 1e-9);
    }

    @Test
    public void powerBindsTighterThanMultiplication() {
        assertEquals(50.0, CalculatorEngine.evaluate("2 * 5 ^ 2"), 1e-9);
    }

    @Test
    public void sameRankOperatorsRunLeftToRight() {
        assertEquals(5.0, CalculatorEngine.evaluate("10 - 3 - 2"), 1e-9);
        assertEquals(1.0, CalculatorEngine.evaluate("8 / 4 / 2"), 1e-9);
    }

    @Test
    public void negativeOperandsAreNotConfusedWithSubtraction() {
        assertEquals(-2.0, CalculatorEngine.evaluate("-5 + 3"), 1e-9);
        assertEquals(8.0, CalculatorEngine.evaluate("5 - -3"), 1e-9);
    }

    @Test
    public void divisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> CalculatorEngine.evaluate("5 / 0"));
    }

    @Test
    public void overflowThrowsInsteadOfReturningInfinity() {
        assertThrows(ArithmeticException.class, () -> CalculatorEngine.evaluate("9 ^ 9 ^ 9 ^ 9"));
    }

    @Test
    public void malformedExpressionsThrow() {
        assertThrows(IllegalArgumentException.class, () -> CalculatorEngine.evaluate("2 +"));
        assertThrows(IllegalArgumentException.class, () -> CalculatorEngine.evaluate(""));
        assertThrows(IllegalArgumentException.class, () -> CalculatorEngine.evaluate("2 + abc"));
    }

    // typing numbers

    @Test
    public void startsAtZero() {
        assertEquals("0", engine.getDisplayText());
        assertEquals("", engine.getExpression());
    }

    @Test
    public void leadingZerosAreDropped() {
        type("007");
        assertEquals("7", engine.getDisplayText());
    }

    @Test
    public void secondDecimalPointIsIgnored() {
        type("1.5.5");
        assertEquals("1.55", engine.getDisplayText());
    }

    @Test
    public void decimalPointAtStartOfNumberAddsLeadingZero() {
        type(".5");
        assertEquals("0.5", engine.getDisplayText());
        type("c+.5");
        assertEquals("0 + 0.5", engine.getDisplayText());
    }

    @Test
    public void invalidDigitsAndOperatorsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> engine.appendDigit("12"));
        assertThrows(IllegalArgumentException.class, () -> engine.appendDigit("a"));
        assertThrows(IllegalArgumentException.class, () -> engine.chooseOperator("%"));
    }

    // operators

    @Test
    public void pressingAnotherOperatorReplacesTheLastOne() {
        type("5+*");
        assertEquals("5 * ", engine.getDisplayText());
    }

    @Test
    public void operatorOnEmptyDisplayStartsFromZero() {
        type("*");
        assertEquals("0 * ", engine.getDisplayText());
    }

    // calculating

    @Test
    public void equalsEvaluatesTheWholeExpression() {
        type("5*3+2=");
        assertEquals("17", engine.getDisplayText());
    }

    @Test
    public void equalsDoesNothingWithoutAnOperator() {
        type("42=");
        assertEquals("42", engine.getDisplayText());
    }

    @Test
    public void equalsDoesNothingWhileAnOperatorIsDangling() {
        type("5+=");
        assertEquals("5 + ", engine.getDisplayText());
    }

    @Test
    public void resultsAreRoundedToTenDecimalPlaces() {
        type("1/3=");
        assertEquals("0.3333333333", engine.getDisplayText());
    }

    @Test
    public void digitAfterResultStartsANewCalculation() {
        type("5+3=4");
        assertEquals("4", engine.getDisplayText());
    }

    @Test
    public void decimalAfterResultStartsANewNumber() {
        type("5+3=.");
        assertEquals("0.", engine.getDisplayText());
    }

    @Test
    public void operatorAfterResultContinuesFromIt() {
        type("5+3=*2=");
        assertEquals("16", engine.getDisplayText());
    }

    // live result

    @Test
    public void liveResultFollowsTheExpression() {
        type("1+2*3");
        assertEquals("7", engine.getLiveResult());
    }

    @Test
    public void liveResultIgnoresATrailingOperator() {
        type("1+2*");
        assertEquals("3", engine.getLiveResult());
    }

    @Test
    public void liveResultIsEmptyWithoutAnOperator() {
        type("5");
        assertEquals("", engine.getLiveResult());
        type("s");
        assertEquals("", engine.getLiveResult());
    }

    @Test
    public void liveResultIsEmptyWhenTheExpressionCannotBeEvaluated() {
        type("5/0");
        assertEquals("", engine.getLiveResult());
    }

    // editing

    @Test
    public void deleteRemovesOneCharacter() {
        type("123d");
        assertEquals("12", engine.getDisplayText());
    }

    @Test
    public void deleteRemovesAWholeOperator() {
        type("2+d");
        assertEquals("2", engine.getDisplayText());
    }

    @Test
    public void deleteDoesNotLeaveALoneMinusSign() {
        type("5sd");
        assertEquals("0", engine.getDisplayText());
        type("c5-3sd");
        assertEquals("5 - ", engine.getDisplayText());
    }

    @Test
    public void deleteOnEmptyExpressionIsHarmless() {
        type("d");
        assertEquals("0", engine.getDisplayText());
    }

    @Test
    public void clearEntryRemovesOnlyTheCurrentNumber() {
        type("12+34e");
        assertEquals("12 + ", engine.getDisplayText());
    }

    @Test
    public void clearAllResetsEverything() {
        type("12+34c");
        assertEquals("0", engine.getDisplayText());
        assertEquals("", engine.getLiveResult());
    }

    // functions on the current number

    @Test
    public void signTogglesOnlyTheCurrentNumber() {
        type("2^3s");
        assertEquals("2 ^ -3", engine.getDisplayText());
        type("s");
        assertEquals("2 ^ 3", engine.getDisplayText());
    }

    @Test
    public void squareAppliesToTheCurrentNumberOnly() {
        type("2^3q");
        assertEquals("2 ^ 9", engine.getDisplayText());
    }

    @Test
    public void squareRootAndReciprocalWork() {
        type("9r");
        assertEquals("3", engine.getDisplayText());
        type("c4i");
        assertEquals("0.25", engine.getDisplayText());
    }

    @Test
    public void percentAloneDividesByOneHundred() {
        type("50%");
        assertEquals("0.5", engine.getDisplayText());
    }

    @Test
    public void percentAfterPlusOrMinusIsRelativeToTheLeftSide() {
        type("200+10%");
        assertEquals("200 + 20", engine.getDisplayText());
        type("=");
        assertEquals("220", engine.getDisplayText());
    }

    @Test
    public void percentAfterMultiplyIsJustAFraction() {
        type("50*10%");
        assertEquals("50 * 0.1", engine.getDisplayText());
    }

    // errors

    @Test
    public void divisionByZeroShowsAnErrorAndClearsTheExpression() {
        type("5/0=");
        assertTrue(engine.hasError());
        assertEquals("Cannot divide by zero", engine.getDisplayText());
        assertEquals("", engine.getExpression());
    }

    @Test
    public void reciprocalOfZeroIsAnError() {
        type("0i");
        assertEquals("Cannot divide by zero", engine.getDisplayText());
    }

    @Test
    public void squareRootOfANegativeIsAnError() {
        type("5sr");
        assertEquals("Invalid input", engine.getDisplayText());
    }

    @Test
    public void overflowIsReportedAsInvalidResult() {
        type("9^9^9^9^9=");
        assertEquals("Invalid result", engine.getDisplayText());
    }

    @Test
    public void anyKeyDismissesAnError() {
        type("5/0=");
        type("7");
        assertFalse(engine.hasError());
        assertEquals("7", engine.getDisplayText());
    }

    // formatting

    @Test
    public void negativeZeroIsShownAsZero() {
        type("0s+0=");
        assertEquals("0", engine.getDisplayText());
    }

    @Test
    public void numbersAreFormattedTheSameInEveryLocale() {
        java.util.Locale saved = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.GERMANY);
            CalculatorEngine localEngine = new CalculatorEngine();
            localEngine.appendDigit("1");
            localEngine.chooseOperator("/");
            localEngine.appendDigit("4");
            localEngine.calculate();
            assertEquals("0.25", localEngine.getDisplayText());
        } finally {
            java.util.Locale.setDefault(saved);
        }
    }
}
