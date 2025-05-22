package include;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Bisection {
    private Stack<String> msgSoln;  // Changed to Stack
    private Stack<String> answers;  // Changed to Stack
    private List<Double> iterationValues;
    private String functionExpression;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;
    private int maxIterations;

    public Bisection() {
        this(0.0001, 1000);
    }

    public Bisection(double tolerance) {
        this(tolerance, 1000);
    }

    public Bisection(double tolerance, int maxIterations) {
        this.msgSoln = new Stack<>();
        this.answers = new Stack<>();
        this.iterationValues = new ArrayList<>();
        this.fixedFormat = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fixedFormat.setDecimalFormatSymbols(symbols);
        setTolerance(tolerance);
        this.maxIterations = maxIterations;
    }

    public Stack<String> getSolutionSteps() {
        return msgSoln;
    }

    public Stack<String> getAnswers() {
        return answers;
    }

    public List<Double> getIterationValues() {
        return iterationValues;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
        updateDecimalFormat();
    }

    private void updateDecimalFormat() {
        int decimalPlaces = Math.max(1, (int) Math.ceil(-Math.log10(tolerance)));
        
        StringBuilder pattern = new StringBuilder("0");
        if (decimalPlaces > 0) {
            pattern.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                pattern.append("0");
            }
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');

        this.decimalFormat = new DecimalFormat(pattern.toString());
        this.decimalFormat.setDecimalFormatSymbols(symbols);

        this.fixedFormat = new DecimalFormat(pattern.toString());
        this.fixedFormat.setDecimalFormatSymbols(symbols);
    }

    private String formatNumber(double value) {
        return decimalFormat.format(value);
    }

    private String formatFixed(double value) {
        return fixedFormat.format(value);
    }

    private String getFunctionEvaluationString(double x) {
        return functionExpression.replaceAll("x", formatFixed(x));
    }
    
    private double f(double x) throws IllegalArgumentException {
        try {
            Expression e = new ExpressionBuilder(functionExpression)
                .variables("x")
                .build()
                .setVariable("x", x);
            return e.evaluate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error evaluating function: " + e.getMessage());
        }
    }

    public boolean solve(String function, double a, double b) {
        return solve(function, a, b, this.tolerance);
    }

    public boolean solve(String function, double a, double b, double tolerance) {
        msgSoln.clear();
        answers.clear();
        iterationValues.clear();
        this.functionExpression = function;
        setTolerance(tolerance);

        msgSoln.push("Using tolerance: " + formatNumber(tolerance)); 
        msgSoln.push("Maximum iterations: " + maxIterations);
        
        try {
            double fa = f(a);
            double fb = f(b);
            
            msgSoln.push("Initial values:");
            msgSoln.push(String.format("a = %s, f(a) = %s = %s", 
                formatNumber(a), getFunctionEvaluationString(a), formatNumber(fa)));
            msgSoln.push(String.format("b = %s, f(b) = %s = %s", 
                formatNumber(b), getFunctionEvaluationString(b), formatNumber(fb)));
            msgSoln.push("");

            if (fa * fb >= 0) {
                msgSoln.push("Bisection method cannot continue. f(a) and f(b) must have opposite signs.");
                return false;
            }

            double root = bisectionRecursive(a, b, fa, fb, 1);

            answers.push("Root found: " + formatNumber(root));
            answers.push("Number of iterations: " + iterationValues.size());
            answers.push("Final tolerance: " + formatNumber(tolerance));
            
            return true;
        } catch (IllegalArgumentException e) {
            msgSoln.push("Error with function evaluation: " + e.getMessage());
            return false;
        }
    }
    
    private double bisectionRecursive(double a, double b, double fa, double fb, int iteration) {
        if (iteration > maxIterations) {
            msgSoln.push("Maximum iterations reached without convergence.");
            return (a + b) / 2;
        }

        double c = (a + b) / 2;
        double fc = f(c);
        iterationValues.add(c);

        msgSoln.push(String.format("Iteration %d:", iteration));
        msgSoln.push(String.format("  a = %s, f(a) = %s = %s", 
            formatNumber(a), getFunctionEvaluationString(a), formatNumber(fa)));
        msgSoln.push(String.format("  b = %s, f(b) = %s = %s", 
            formatNumber(b), getFunctionEvaluationString(b), formatNumber(fb)));
        msgSoln.push(String.format("  c = (a + b)/2 = (%s + %s)/2 = %s", 
            formatNumber(a), formatNumber(b), formatNumber(c)));
        msgSoln.push(String.format("  f(c) = %s = %s", 
            getFunctionEvaluationString(c), formatNumber(fc)));
        msgSoln.push("");

        if (Math.abs(fc) < tolerance || (b - a) / 2 < tolerance) {
            return c;
        }

        if (fc * fa < 0) {
            return bisectionRecursive(a, c, fa, fc, iteration + 1);
        } else {
            return bisectionRecursive(c, b, fc, fb, iteration + 1);
        }
    }
    
    public void printSolution(boolean success) {
        System.out.println(functionExpression);
        System.out.println(tolerance);
        System.out.println();

        if (success) {
            System.out.println("Solution Steps:");
            List<String> reversedSteps = new ArrayList<>(msgSoln);
            Collections.reverse(reversedSteps);
            for (String step : reversedSteps) {
                System.out.println(step);
            }
            
            System.out.println("\nFinal Answers:");
            List<String> reversedAnswers = new ArrayList<>(answers);
            Collections.reverse(reversedAnswers);
            for (String answer : reversedAnswers) {
                System.out.println(answer);
            }
        } else {
            System.out.println("Solution failed:");
            List<String> reversedErrors = new ArrayList<>(msgSoln);
            Collections.reverse(reversedErrors);
            for (String error : reversedErrors) {
                System.out.println(error);
            }
        }
    }

    public static void main(String[] args) {
        //? Example usages:
        
        // 3. Specifying tolerance at solve time (0.00001)
        Bisection solver3 = new Bisection();
        boolean success3 = solver3.solve("x^3 - 4cos(x)", 1.0, 2.0, 0.1);
        solver3.printSolution(success3);
    }
}