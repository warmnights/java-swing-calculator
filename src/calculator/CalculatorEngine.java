package calculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The calculator's state and logic. It knows nothing about Swing, so it can
 * be driven and tested without opening a window.
 *
 * The expression is kept as plain text such as "1 + 2 * 3". Operators are
 * always surrounded by single spaces, which is what lets a leading minus sign
 * on a number ("-5") be told apart from the subtraction operator.
 */
public class CalculatorEngine {

    private static final String OPERATORS = "+-*/^";
    private static final Pattern OPERATOR_PATTERN = Pattern.compile(" [+\\-*/^] ");

    private final DecimalFormat numberFormat =
        new DecimalFormat("0.##########", DecimalFormatSymbols.getInstance(Locale.US));

    private String expression = "";
    private String errorMessage = null;
    private boolean justCalculated = false;


    /** What the main display should show: an error message, the expression, or "0". */
    public String getDisplayText() {
        if (errorMessage != null) {
            return errorMessage;
        }
        return expression.isEmpty() ? "0" : expression;
    }

    public String getExpression() {
        return expression;
    }

    public boolean hasError() {
        return errorMessage != null;
    }

    /** The running total of the expression so far, or an empty string if there isn't one. */
    public String getLiveResult() {
        if (errorMessage != null) {
            return "";
        }

        String text = endsWithOperator()
            ? expression.substring(0, expression.length() - 3)
            : expression;

        if (!hasOperator(text)) {
            return "";
        }

        try {
            return formatNumber(evaluate(text));
        } catch (ArithmeticException e) {
            return "";
        }
    }


    public void appendDigit(String digit) {
        if (digit.length() != 1 || digit.charAt(0) < '0' || digit.charAt(0) > '9') {
            throw new IllegalArgumentException("Not a digit: " + digit);
        }

        errorMessage = null;
        startFreshIfCalculated();

        String current = currentNumber();

        if (current.equals("0") || current.equals("-0")) {
            expression = expression.substring(0, expression.length() - 1);
        }

        expression += digit;
    }

    public void appendDecimal() {
        errorMessage = null;
        startFreshIfCalculated();

        String current = currentNumber();

        if (current.contains(".")) {
            return;
        }

        expression += current.isEmpty() ? "0." : ".";
    }

    public void chooseOperator(String operator) {
        if (operator.length() != 1 || !OPERATORS.contains(operator)) {
            throw new IllegalArgumentException("Not an operator: " + operator);
        }

        errorMessage = null;

        if (expression.isEmpty()) {
            expression = "0";
        }

        if (endsWithOperator()) {
            expression = expression.substring(0, expression.length() - 3);
        }

        expression += " " + operator + " ";
        justCalculated = false;
    }

    public void toggleSign() {
        errorMessage = null;
        String current = currentNumber();

        if (current.isEmpty()) {
            return;
        }

        replaceCurrentNumber(current.startsWith("-") ? current.substring(1) : "-" + current);
    }


    public void squareRoot() {
        transformCurrentNumber(x -> {
            if (x < 0) {
                throw new ArithmeticException("Invalid input");
            }
            return Math.sqrt(x);
        });
    }

    public void square() {
        transformCurrentNumber(x -> x * x);
    }

    public void reciprocal() {
        transformCurrentNumber(x -> {
            if (x == 0) {
                throw new ArithmeticException("Cannot divide by zero");
            }
            return 1.0 / x;
        });
    }

    public void percent() {
        errorMessage = null;
        String current = currentNumber();

        if (current.isEmpty()) {
            return;
        }

        try {
            double number = Double.parseDouble(current);
            double result = number / 100.0;

            Matcher m = OPERATOR_PATTERN.matcher(expression);
            int operatorStart = -1;
            String operator = "";

            while (m.find()) {
                operatorStart = m.start();
                operator = m.group().trim();
            }

            // 200 + 10% means 10% of 200, not 0.1
            if (operator.equals("+") || operator.equals("-")) {
                double base = evaluate(expression.substring(0, operatorStart));
                result = base * number / 100.0;
            }

            replaceCurrentNumber(formatNumber(result));
        } catch (ArithmeticException e) {
            fail(e.getMessage());
        }
    }


    public void clearEntry() {
        errorMessage = null;
        expression = expression.substring(0, currentNumberStart());
        justCalculated = false;
    }

    public void clearAll() {
        errorMessage = null;
        expression = "";
        justCalculated = false;
    }

    public void deleteLast() {
        errorMessage = null;

        if (expression.isEmpty()) {
            return;
        }

        int cut = endsWithOperator() ? 3 : 1;
        expression = expression.substring(0, expression.length() - cut);

        // don't leave a lone minus sign behind
        if (currentNumber().equals("-")) {
            expression = expression.substring(0, currentNumberStart());
        }

        justCalculated = false;
    }

    public void calculate() {
        errorMessage = null;

        if (!hasOperator(expression) || endsWithOperator()) {
            return;
        }

        try {
            expression = formatNumber(evaluate(expression));
            justCalculated = true;
        } catch (ArithmeticException e) {
            fail(e.getMessage());
        }
    }


    /**
     * Evaluates an expression written with spaces between every token, for
     * example "2 + 3 * 4 ^ 2". Precedence is ^, then * and /, then + and -;
     * operators of equal rank are applied left to right.
     *
     * @throws ArithmeticException      for division by zero or a result that is not finite
     * @throws IllegalArgumentException if the expression is malformed
     */
    public static double evaluate(String input) {
        List<Double> numbers = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        for (String token : input.trim().split("\\s+")) {
            if (token.length() == 1 && OPERATORS.contains(token)) {
                operators.add(token);
            } else {
                numbers.add(Double.parseDouble(token));
            }
        }

        if (numbers.size() != operators.size() + 1) {
            throw new IllegalArgumentException("Malformed expression: " + input);
        }

        reduce(numbers, operators, "^");
        reduce(numbers, operators, "*/");
        reduce(numbers, operators, "+-");

        double result = numbers.get(0);

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new ArithmeticException("Invalid result");
        }

        return result;
    }

    private static void reduce(List<Double> numbers, List<String> operators, String handled) {
        int i = 0;

        while (i < operators.size()) {
            String operator = operators.get(i);

            if (handled.contains(operator)) {
                numbers.set(i, applyOperator(numbers.get(i), operator, numbers.get(i + 1)));
                numbers.remove(i + 1);
                operators.remove(i);
            } else {
                i++;
            }
        }
    }

    private static double applyOperator(double a, String operator, double b) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return a / b;
            default:
                return Math.pow(a, b);
        }
    }


    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new ArithmeticException("Invalid result");
        }

        String text = numberFormat.format(value);
        return text.equals("-0") ? "0" : text;
    }

    private boolean hasOperator(String text) {
        return OPERATOR_PATTERN.matcher(text).find();
    }

    private boolean endsWithOperator() {
        return expression.endsWith(" ");
    }

    private int currentNumberStart() {
        Matcher m = OPERATOR_PATTERN.matcher(expression);
        int start = 0;

        while (m.find()) {
            start = m.end();
        }

        return start;
    }

    private String currentNumber() {
        return expression.substring(currentNumberStart());
    }

    private void replaceCurrentNumber(String value) {
        expression = expression.substring(0, currentNumberStart()) + value;
    }

    private void startFreshIfCalculated() {
        if (justCalculated) {
            expression = "";
            justCalculated = false;
        }
    }

    private void transformCurrentNumber(DoubleUnaryOperator function) {
        errorMessage = null;
        String current = currentNumber();

        if (current.isEmpty()) {
            return;
        }

        try {
            double result = function.applyAsDouble(Double.parseDouble(current));
            replaceCurrentNumber(formatNumber(result));
        } catch (ArithmeticException e) {
            fail(e.getMessage());
        }
    }

    private void fail(String message) {
        expression = "";
        justCalculated = false;
        errorMessage = message;
    }
}
