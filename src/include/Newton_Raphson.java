package include;

import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Newton_Raphson {
    private List<String> msgSoln;
    private List<String> answers;
    private Queue<Double> iterationValues;  // Changed from List<Double> to Queue<Double>
    private String functionExpression;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;
    private int maxIterations;
    private double derivativeStepSize;

    public Newton_Raphson() {
        this(0.0001, 1000, 1e-5);
    }

    public Newton_Raphson(double tolerance) {
        this(tolerance, 1000, 1e-5);
    }

    public Newton_Raphson(double tolerance, int maxIterations, double derivativeStepSize) {
        this.msgSoln = new LinkedList<>();
        this.answers = new LinkedList<>();
        this.iterationValues = new LinkedList<>(); // LinkedList implements Queue
        this.fixedFormat = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fixedFormat.setDecimalFormatSymbols(symbols);
        setTolerance(tolerance);
        this.maxIterations = maxIterations;
        this.derivativeStepSize = derivativeStepSize;
    }

    public List<String> getSolutionSteps() {
        return msgSoln;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public Queue<Double> getIterationValues() {  // Updated return type
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
        try {
            double fxh = f(x + derivativeStepSize);
            double fx = f(x - derivativeStepSize);
            return (fxh - fx) / (2 * derivativeStepSize);
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
        msgSoln.add("Derivative step size: " + derivativeStepSize);
        
        try {
            msgSoln.add("Starting Newton-Raphson method with initial guess: " + formatNumber(initialGuess));
            msgSoln.add("");

            double root = newtonRaphsonRecursive(initialGuess, 1);

            answers.add("Root found: " + formatNumber(root));
            answers.add("Number of iterations: " + iterationValues.size());
            answers.add("Final tolerance: " + formatNumber(tolerance));
            
            return true;
        } catch (IllegalArgumentException e) {
            msgSoln.add("Error: " + e.getMessage());
            return false;
        }
    }
    
    private double newtonRaphsonRecursive(double x, int iteration) {
        if (iteration > maxIterations) {
            msgSoln.add("Maximum iterations reached without convergence.");
            return x;
        }

        double fx = f(x);
        double dfx = derivative(x);
        iterationValues.add(x);  // Queue's add() method

        msgSoln.add(String.format("Iteration %d:", iteration));
        msgSoln.add(String.format("  x%d = %s", iteration, formatNumber(x)));
        msgSoln.add(String.format("  f(x%d) = %s = %s", 
            iteration, getFunctionEvaluationString(x), formatNumber(fx)));
        msgSoln.add(String.format("  f'(x%d) = [f(x+h)-f(x-h)]/(2h) = %s", 
            iteration, formatNumber(dfx)));

        if (Math.abs(dfx) < 1e-10) {
            msgSoln.add("Error: Derivative too small (near zero), division by zero risk.");
            return x;
        }

        double xNew = x - fx / dfx;
        msgSoln.add(String.format("  x%d = x - f(x)/f'(x) = %s - (%s)/(%s) = %s", 
            iteration+1, formatNumber(x), formatNumber(fx), formatNumber(dfx), formatNumber(xNew)));
        msgSoln.add("");

        if (Math.abs(xNew - x) < tolerance) {
            msgSoln.add("Convergence achieved!");
            return xNew;
        }

        return newtonRaphsonRecursive(xNew, iteration + 1);
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
        Newton_Raphson solver1 = new Newton_Raphson();
        boolean success1 = solver1.solve("x^3 - x - 1", 1.5);

        System.out.println();
        solver1.printSolution(success1);

        System.out.println("\n--------------------------------\n");
        
        // 2. Specifying custom tolerance (0.001)
        Newton_Raphson solver2 = new Newton_Raphson(0.001);
        boolean success2 = solver2.solve("x^3 - x - 1", 1.5);
        solver2.printSolution(success2);

        System.out.println("\n--------------------------------\n");
        
        // 3. Specifying tolerance at solve time (0.00001)
        Newton_Raphson solver3 = new Newton_Raphson();
        boolean success3 = solver3.solve("cos(x) - x", 0.5, 0.00001);
        solver3.printSolution(success3);
    }
}