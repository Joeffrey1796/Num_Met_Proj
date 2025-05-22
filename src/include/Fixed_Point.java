package include;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Stack;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Fixed_Point {
    private LinkedList<String> msgSoln;
    private LinkedList<String> answers;
    private Stack<Double> iterationValues;
    private String functionExpression;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;
    private int maxIterations;

    public Fixed_Point() {
        this(0.0001, 1000);
    }

    public Fixed_Point(double tolerance) {
        this(tolerance, 1000);
    }

    public Fixed_Point(double tolerance, int maxIterations) {
        this.msgSoln = new LinkedList<>();
        this.answers = new LinkedList<>();
        this.iterationValues = new Stack<>();
        this.fixedFormat = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fixedFormat.setDecimalFormatSymbols(symbols);
        setTolerance(tolerance);
        this.maxIterations = maxIterations;
    }

    public LinkedList<String> getSolutionSteps() {
        return msgSoln;
    }

    public LinkedList<String> getAnswers() {
        return answers;
    }

    public Stack<Double> getIterationValues() {
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

    private double derivative(double x) throws IllegalArgumentException {
        double h = 1e-10;
        try {
            double fxh = f(x + h);
            double fx = f(x);
            return (fxh - fx) / h;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error calculating derivative: " + e.getMessage());
        }
    }

    public boolean solve(String function, double initialGuess) {
        return solve(function, initialGuess, this.tolerance);
    }

    public boolean solve(String function, double initialGuess, double tolerance) {
        msgSoln.clear();
        answers.clear();
        iterationValues.clear();
        this.functionExpression = function;
        setTolerance(tolerance);

        msgSoln.add("Using tolerance: " + formatNumber(tolerance));
        msgSoln.add("Maximum iterations: " + maxIterations);
        
        try {
            double derivativeValue = derivative(initialGuess);
            msgSoln.add(String.format("Derivative at initial guess: g'(%s) = %s", 
                formatNumber(initialGuess), formatNumber(derivativeValue)));
            
            if (Math.abs(derivativeValue) >= 1) {
                msgSoln.add("Warning: |g'(x)| ≥ 1 at initial guess. Convergence not guaranteed.");
            }

            msgSoln.add("Starting fixed-point iteration with initial guess: " + formatNumber(initialGuess));
            msgSoln.add("");

            double root = fixedPointRecursive(initialGuess, 1);

            answers.add("Root found: " + formatNumber(root));
            answers.add("Number of iterations: " + iterationValues.size());
            answers.add("Final tolerance: " + formatNumber(tolerance));
            
            return true;
        } catch (IllegalArgumentException e) {
            msgSoln.add("Error: " + e.getMessage());
            return false;
        }
    }
    
    private double fixedPointRecursive(double x, int iteration) {
        if (iteration > maxIterations) {
            msgSoln.add("Maximum iterations reached without convergence.");
            return x;
        }

        double gx = f(x);
        double error = Math.abs(gx - x);
        iterationValues.push(x);

        msgSoln.add(String.format("Iteration %d:", iteration));
        msgSoln.add(String.format("  x%d = %s", iteration, formatNumber(x)));
        msgSoln.add(String.format("  g(x%d) = %s = %s", 
            iteration, getFunctionEvaluationString(x), formatNumber(gx)));
        msgSoln.add(String.format("  Error = |g(x) - x| = |%s - %s| = %s", 
            formatNumber(gx), formatNumber(x), formatNumber(error)));
        msgSoln.add("");

        if (error < tolerance) {
            msgSoln.add("Convergence achieved!");
            return gx;
        }

        return fixedPointRecursive(gx, iteration + 1);
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
        //? Example usages:
        
        // 1. Using default tolerance (0.0001)
        Fixed_Point solver1 = new Fixed_Point();
        boolean success1 = solver1.solve("(x+1)^(1/2)", 1.0);

        System.out.println();
        solver1.printSolution(success1);

        System.out.println("\n--------------------------------\n");
        
        // 2. Specifying custom tolerance (0.001)
        Fixed_Point solver2 = new Fixed_Point(0.001);
        boolean success2 = solver2.solve("(x+1)^(1/2)", 1.0);
        solver2.printSolution(success2);

        System.out.println("\n--------------------------------\n");
        
        // 3. Specifying tolerance at solve time (0.00001)
        Fixed_Point solver3 = new Fixed_Point();
        boolean success3 = solver3.solve("cos(2*x)", 0.5, 0.00001);
        solver3.printSolution(success3);
    }
}