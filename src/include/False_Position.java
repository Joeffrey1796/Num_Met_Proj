package include;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class False_Position {
    private List<String> msgSoln;
    private List<String> answers;
    private List<Double> iterationValues;
    private String functionExpression;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;
    private int maxIterations;

    public False_Position() {
        this(0.0001, 1000);
    }

    public False_Position(double tolerance) {
        this(tolerance, 1000);
    }

    public False_Position(double tolerance, int maxIterations) {
        this.msgSoln = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.iterationValues = new ArrayList<>();
        this.fixedFormat = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fixedFormat.setDecimalFormatSymbols(symbols);
        setTolerance(tolerance);
        this.maxIterations = maxIterations;
    }

    public List<String> getSolutionSteps() {
        return msgSoln;
    }

    public List<String> getAnswers() {
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

        msgSoln.add("Using tolerance: " + formatNumber(tolerance));
        msgSoln.add("Maximum iterations: " + maxIterations);
        
        try {
            double fa = f(a);
            double fb = f(b);
            
            msgSoln.add("Initial values:");
            msgSoln.add(String.format("a = %s, f(a) = %s = %s", 
                formatNumber(a), getFunctionEvaluationString(a), formatNumber(fa)));
            msgSoln.add(String.format("b = %s, f(b) = %s = %s", 
                formatNumber(b), getFunctionEvaluationString(b), formatNumber(fb)));
            msgSoln.add("");

            if (fa * fb >= 0) {
                msgSoln.add("False Position method cannot continue. f(a) and f(b) must have opposite signs.");
                return false;
            }

            double root = falsePositionRecursive(a, b, fa, fb, 1);

            answers.add("Root found: " + formatNumber(root));
            answers.add("Number of iterations: " + iterationValues.size());
            answers.add("Final tolerance: " + formatNumber(tolerance));
            
            return true;
        } catch (IllegalArgumentException e) {
            msgSoln.add("Error with function evaluation: " + e.getMessage());
            return false;
        }
    }
    
    private double falsePositionRecursive(double a, double b, double fa, double fb, int iteration) {
        if (iteration > maxIterations) {
            msgSoln.add("Maximum iterations reached without convergence.");
            return (a + b) / 2;
        }

        // Calculate c using false position formula
        double c = a - (fa * (b - a)) / (fb - fa);
        double fc = f(c);
        iterationValues.add(c);

        msgSoln.add(String.format("Iteration %d:", iteration));
        msgSoln.add(String.format("  a = %s, f(a) = %s = %s", 
            formatNumber(a), getFunctionEvaluationString(a), formatNumber(fa)));
        msgSoln.add(String.format("  b = %s, f(b) = %s = %s", 
            formatNumber(b), getFunctionEvaluationString(b), formatNumber(fb)));
        msgSoln.add(String.format("  c = a - (f(a)*(b-a))/(f(b)-f(a)) = %s - (%s*(%s-%s))/(%s-%s) = %s", 
            formatNumber(a), formatNumber(fa), formatNumber(b), formatNumber(a),
            formatNumber(fb), formatNumber(fa), formatNumber(c)));
        msgSoln.add(String.format("  f(c) = %s = %s", 
            getFunctionEvaluationString(c), formatNumber(fc)));
        msgSoln.add("");

        // Check for convergence
        if (Math.abs(fc) < tolerance || Math.abs(b - a) < tolerance) {
            return c;
        }

        // Check for stagnation
        if (c == a || c == b) {
            return c;
        }

        // Recursive step
        if (fa * fc < 0) {
            return falsePositionRecursive(a, c, fa, fc, iteration + 1);
        } else {
            return falsePositionRecursive(c, b, fc, fb, iteration + 1);
        }
    }
    
    public void printSolution(boolean success) {
        System.out.println(functionExpression);
        System.out.println(tolerance);
        System.out.println();

        if (success) {
            System.out.println("Solution Steps:");
            for (String step : msgSoln) {
                System.out.println(step);
            }
            
            System.out.println("\nFinal Answers:");
            for (String answer : answers) {
                System.out.println(answer);
            }
        } else {
            System.out.println("Solution failed:");
            for (String error : msgSoln) {
                System.out.println(error);
            }
        }
    }

    public static void main(String[] args) {
        // Example usages:
        
        // 1. Using default tolerance (0.0001)
        False_Position solver1 = new False_Position();
        boolean success1 = solver1.solve("x^3 - x - 1", 1.0, 2.0);

        System.out.println();
        solver1.printSolution(success1);

        System.out.println("\n--------------------------------\n");
        
        // 2. Specifying custom tolerance (0.001)
        False_Position solver2 = new False_Position(0.000001);
        boolean success2 = solver2.solve("x^3 - x - 1", 1.0, 2.0);
        solver2.printSolution(success2);

        System.out.println("\n--------------------------------\n");
        
        // 3. Specifying tolerance at solve time (0.00001)
        False_Position solver3 = new False_Position();
        boolean success3 = solver3.solve("x^3 - x - 1", 1.0, 2.0, 0.1);
        solver3.printSolution(success3);
    }
}