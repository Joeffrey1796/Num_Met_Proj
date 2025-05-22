package include;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import include.utils.Fraction;

public class Cramers_Rule {
    private List<String> msgSoln;
    private List<String> answers;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;

    public Cramers_Rule() {
        this(0.01);
    }

    public Cramers_Rule(double tolerance) {
        this.msgSoln = new LinkedList<>();
        this.answers = new LinkedList<>();
        this.fixedFormat = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        fixedFormat.setDecimalFormatSymbols(symbols);
        setTolerance(tolerance);
    }

    public List<String> getSolutionSteps() {
        return msgSoln;
    }

    public List<String> getAnswers() {
        return answers;
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

    private LinkedList<LinkedList<Double>> convertToLinkedList(double[][] matrix) {
        LinkedList<LinkedList<Double>> list = new LinkedList<>();
        for (double[] row : matrix) {
            LinkedList<Double> listRow = new LinkedList<>();
            for (double val : row) {
                listRow.add(val);
            }
            list.add(listRow);
        }
        return list;
    }

    private LinkedList<Double> convertToLinkedList(double[] vector) {
        LinkedList<Double> list = new LinkedList<>();
        for (double val : vector) {
            list.add(val);
        }
        return list;
    }

    private String matrixToString(LinkedList<LinkedList<Double>> matrix) {
        StringBuilder sb = new StringBuilder();
        for (LinkedList<Double> row : matrix) {
            for (Double entry : row) {
                sb.append(formatNumber(entry)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private LinkedList<LinkedList<Double>> replaceColumn(LinkedList<LinkedList<Double>> matrix, 
                                                      LinkedList<Double> column, 
                                                      int colIndex) {
        LinkedList<LinkedList<Double>> newMatrix = new LinkedList<>();
        for (LinkedList<Double> row : matrix) {
            LinkedList<Double> newRow = new LinkedList<>(row);
            newRow.set(colIndex, column.get(newMatrix.size()));
            newMatrix.add(newRow);
        }
        return newMatrix;
    }

    private double determinant(LinkedList<LinkedList<Double>> matrix) {
        int n = matrix.size();
        if (n == 1) return matrix.get(0).get(0);
        if (n == 2) {
            return matrix.get(0).get(0) * matrix.get(1).get(1) - 
                   matrix.get(0).get(1) * matrix.get(1).get(0);
        }

        double det = 0;
        for (int i = 0; i < n; i++) {
            det += Math.pow(-1, i) * matrix.get(0).get(i) * 
                  determinant(minor(matrix, 0, i));
        }
        return det;
    }

    private LinkedList<LinkedList<Double>> minor(LinkedList<LinkedList<Double>> matrix, 
                                              int row, int col) {
        LinkedList<LinkedList<Double>> minor = new LinkedList<>();
        for (int i = 0; i < matrix.size(); i++) {
            if (i == row) continue;
            LinkedList<Double> newRow = new LinkedList<>();
            for (int j = 0; j < matrix.size(); j++) {
                if (j == col) continue;
                newRow.add(matrix.get(i).get(j));
            }
            minor.add(newRow);
        }
        return minor;
    }

    public boolean solve(double[][] A, double[] B) {
        msgSoln.clear();
        answers.clear();

        LinkedList<LinkedList<Double>> listA = convertToLinkedList(A);
        LinkedList<Double> listB = convertToLinkedList(B);

        int n = listA.size();
        
        if (n < 2) {
            msgSoln.add("Error: System must have at least 2 variables.");
            return false;
        }
        
        if (listB.size() != n) {
            msgSoln.add("Error: Constant matrix is not compatible with coefficient matrix.");
            return false;
        }

        msgSoln.add("Original Coefficient Matrix:");
        msgSoln.add(matrixToString(listA));
        msgSoln.add("Constant Terms Vector:");
        for (int i = 0; i < n; i++) {
            msgSoln.add("b[" + i + "] = " + formatNumber(listB.get(i)));
        }
        msgSoln.add("");

        double detAValue = determinant(listA);
        Fraction detA = new Fraction(detAValue);
        msgSoln.add("Step 1: Calculate determinant of A");
        msgSoln.add("det(A) = " + detA + " ≈ " + formatNumber(detAValue));
        msgSoln.add("");

        if (Math.abs(detAValue) < tolerance) {
            msgSoln.add("Error: Determinant of A is 0. No unique solution exists.");
            return false;
        }

        for (int i = 0; i < n; i++) {
            LinkedList<LinkedList<Double>> Ai = replaceColumn(listA, listB, i);
            msgSoln.add("Step " + (i + 2) + ": Matrix A" + (i + 1) + 
                       " (replace column " + (i + 1) + " with constants)");
            msgSoln.add(matrixToString(Ai));
            
            double detAiValue = determinant(Ai);
            Fraction detAi = new Fraction(detAiValue);
            msgSoln.add("det(A" + (i + 1) + ") = " + detAi + " ≈ " + formatNumber(detAiValue));
            
            Fraction xi = detAi.divide(detA);
            msgSoln.add("x" + (i + 1) + " = det(A" + (i + 1) + ")/det(A) = " + 
                        xi + " ≈ " + formatNumber(xi.doubleValue()));
            msgSoln.add("");
            
            answers.add("x" + (i + 1) + " = " + xi + " ≈ " + formatNumber(xi.doubleValue()));
        }

        return true;
    }
    public void printSolution(boolean success) {
        System.out.println("Cramer's Rule Solution");
        System.out.println("Tolerance: " + tolerance);
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
        //? Example usage:
        
        // 1. Using default tolerance (0.0001)
        Cramers_Rule solver1 = new Cramers_Rule();
        double[][] matrixA = {
            {2,-1},
            {1,1}
        };

        double[] matrixB = {5,4};
        boolean success1 = solver1.solve(matrixA, matrixB);
        solver1.printSolution(success1);

        System.out.println("\n--------------------------------\n");
        
        // 2. Specifying custom tolerance (0.001)
        Cramers_Rule solver2 = new Cramers_Rule(0.001);
        boolean success2 = solver2.solve(matrixA, new double[]{5, 1, 8});
        solver2.printSolution(success2);
    }
}