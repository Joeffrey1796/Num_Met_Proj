package include;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Secant_Method {
    private List<String> msgSoln;
    private List<String> answers;
    private List<Double> iterationValues;
    private String functionExpression;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;
    private String variable;

    // Default constructor with default variable "x"
    public Secant_Method() {
        this(0.0001, "x");
    }

    // Constructor with tolerance only
    public Secant_Method(double tolerance) {
        this(tolerance, "x");
    }

    // Constructor with variable name
    public Secant_Method(String var) {
        this(0.0001, var);
    }

    // Full constructor with tolerance and variable name
    public Secant_Method(double tolerance, String var) {
        this.msgSoln = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.iterationValues = new ArrayList<>();
        this.fixedFormat = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fixedFormat.setDecimalFormatSymbols(symbols);
        setTolerance(tolerance);
        this.variable = var;
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
        return functionExpression.replaceAll(variable, formatFixed(x));
    }
    
    private double f(double x) throws IllegalArgumentException {
        try {
            Expression e = new ExpressionBuilder(functionExpression)
                .variables(variable)
                .build()
                .setVariable(variable, x);
            return e.evaluate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error evaluating function: " + e.getMessage());
        }
    }

    public boolean solve(String function, double x0, double x1) {
        return solve(function, x0, x1, this.tolerance);
    }

    public boolean solve(String function, double x0, double x1, double tolerance) {
        msgSoln.clear();
        answers.clear();
        iterationValues.clear();
        this.functionExpression = function;
        setTolerance(tolerance);

        msgSoln.add("Using tolerance: " + formatNumber(tolerance));
        
        try {
            double fx0 = f(x0);
            double fx1 = f(x1);
            
            msgSoln.add("Initial values:");
            msgSoln.add(String.format("x0 = %s, f(x0) = %s = %s", 
                formatNumber(x0), getFunctionEvaluationString(x0), formatNumber(fx0)));
            msgSoln.add(String.format("x1 = %s, f(x1) = %s = %s", 
                formatNumber(x1), getFunctionEvaluationString(x1), formatNumber(fx1)));
            msgSoln.add("");

            if (fx0 == fx1) {
                msgSoln.add("Secant method cannot continue. f(x0) and f(x1) are equal.");
                return false;
            }

            double root = secantRecursive(x0, x1, 1);

            answers.add("Root found: " + formatNumber(root));
            answers.add("Number of iterations: " + iterationValues.size());
            answers.add("Final tolerance: " + formatNumber(tolerance));
            
            return true;
        } catch (IllegalArgumentException e) {
            msgSoln.add("Error with function evaluation: " + e.getMessage());
            return false;
        }
    }
    
    private double secantRecursive(double x0, double x1, int iteration) {
        double f0 = f(x0);
        double f1 = f(x1);

        if (f1 - f0 == 0) {
            msgSoln.add("Error: Division by zero in the Secant formula.");
            return x1;
        }

        double x2 = x1 - f1 * (x1 - x0) / (f1 - f0);
        iterationValues.add(x2);

        msgSoln.add(String.format("Iteration %d:", iteration));
        msgSoln.add(String.format("  x%d = %s, f(x%d) = %s = %s", 
            iteration-1, formatNumber(x0), iteration-1, getFunctionEvaluationString(x0), formatNumber(f0)));
        msgSoln.add(String.format("  x%d = %s, f(x%d) = %s = %s", 
            iteration, formatNumber(x1), iteration, getFunctionEvaluationString(x1), formatNumber(f1)));
        msgSoln.add(String.format("  x%d = %s - (%s * (%s - %s)) / (%s - %s) = %s",
            iteration+1, formatNumber(x1), formatNumber(f1), formatNumber(x1), 
            formatNumber(x0), formatNumber(f1), formatNumber(f0), formatNumber(x2)));
        msgSoln.add(String.format("  New approximation: x%d = %s, f(x%d) = %s = %s",
            iteration+1, formatNumber(x2), iteration+1, getFunctionEvaluationString(x2), formatNumber(f(x2))));
        msgSoln.add("");

        if (Math.abs(x2 - x1) < tolerance) {
            return x2;
        }

        return secantRecursive(x1, x2, iteration + 1);
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
        // Example usages showing all constructor variations:
        
        // 1. Using default constructor
        Secant_Method solver1 = new Secant_Method();
        boolean success1 = solver1.solve("x^3 + x - 1", 0.0, 1.0);
        solver1.printSolution(success1);

        System.out.println("\n--------------------------------\n");
        
        // 2. Using constructor with variable name
        Secant_Method solver2 = new Secant_Method("y");
        boolean success2 = solver2.solve("y^3 + y - 1", 0.0, 1.0);
        solver2.printSolution(success2);

        System.out.println("\n--------------------------------\n");
        
        // 3. Using constructor with tolerance
        Secant_Method solver3 = new Secant_Method(0.001);
        boolean success3 = solver3.solve("x^3 + x - 1", 0.0, 1.0);
        solver3.printSolution(success3);

        System.out.println("\n--------------------------------\n");
        
        // 4. Specifying tolerance at solve time
        Secant_Method solver4 = new Secant_Method();
        boolean success4 = solver4.solve("x^3 + x - 1", 0.0, 1.0, 0.1);
        solver4.printSolution(success4);
    }
}