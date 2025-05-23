package include;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class Gauss_Seidel {
    private double[][] coefficients;
    private double[] constants;
    private List<String> solutionSteps;
    private List<String> answers;
    private List<double[]> iterationValues;
    private static final int MAX_ITERATIONS = 1000;
    private double tolerance;
    private DecimalFormat decimalFormat;
    private DecimalFormat fixedFormat;

    public Gauss_Seidel(){
        this(0.001);
    }

    public Gauss_Seidel(double tolerance) {
        this.solutionSteps = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.iterationValues = new ArrayList<>();
        this.tolerance = tolerance;
    }

    public List<String> getSolutionSteps() {
        return solutionSteps;
    }

    public List<String> getAnswers() {
        return answers;
    }

    private void setTolerance(double tolerance) {
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

        this.fixedFormat = new DecimalFormat("0.000");
        this.fixedFormat.setDecimalFormatSymbols(symbols);
    }

    private String formatNumber(double value) {
        return decimalFormat.format(value);
    }

    private String formatFixed(double value) {
        return fixedFormat.format(value);
    }

    private double[][] deepCopyMatrix(double[][] matrix) {
        double[][] copy = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }

    public void printSystem() {
        solutionSteps.add("Linear System:");
        int n = coefficients.length;
        for (int i = 0; i < n; i++) {
            StringBuilder equation = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (j > 0) equation.append(" + ");
                equation.append(String.format("%s x%d", formatFixed(coefficients[i][j]), j + 1));
            }
            equation.append(String.format(" = %s", formatFixed(constants[i])));
            solutionSteps.add(equation.toString());
        }
    }

    public void diagonallyDominant() {
        int n = coefficients.length;
        boolean[] used = new boolean[n];
        double[][] newCoefficients = new double[n][n];
        double[] newConstants = new double[n];

        for (int i = 0; i < n; i++) {
            boolean found = false;
            for (int j = 0; j < n; j++) {
                if (used[j]) continue;
                double diag = Math.abs(coefficients[j][i]);
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    if (k != i) sum += Math.abs(coefficients[j][k]);
                }
                if (diag >= sum) {
                    System.arraycopy(coefficients[j], 0, newCoefficients[i], 0, n);
                    newConstants[i] = constants[j];
                    used[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                solutionSteps.add("Warning: Could not make matrix diagonally dominant. Results may not converge.");
                return;
            }
        }
        for (double[] d :newCoefficients) {
            for (double e : d) {
                System.out.println(e +" ");
            }
            System.out.println();
        }

        for (double d : newConstants) {
            System.out.println(d);
        }
        this.coefficients = newCoefficients;
        this.constants = newConstants;
    }

    public boolean makeDiagonallyDominant() {
        boolean[] visited = new boolean[coefficients.length];
        int[] rowOrder = new int[coefficients.length];
        
        Arrays.fill(visited, false);
        boolean result = transformToDominant(0, visited, rowOrder);

        if (result) {
            solutionSteps.add("\nSystem was rearranged to be diagonally dominant");
        } else {
            solutionSteps.add("\nWarning: System is not diagonally dominant - convergence not guaranteed");
        }
        return result;
    }

    private boolean transformToDominant(int r, boolean[] visited, int[] rowOrder) {
        int n = coefficients.length;
        if (r == n) {
            double[][] newCoefficients = new double[n][n];
            double[] newConstants = new double[n];

            for (int i = 0; i < n; i++) {
                System.arraycopy(coefficients[rowOrder[i]], 0, newCoefficients[i], 0, n);
                newConstants[i] = constants[rowOrder[i]];
            }

            coefficients = newCoefficients;
            constants = newConstants;
            return true;
        }

        for (int i = 0; i < n; i++) {
            if (visited[i]) continue;

            double sum = 0;
            for (int j = 0; j < n; j++) {
                sum += Math.abs(coefficients[i][j]);
            }

            if (2 * Math.abs(coefficients[i][r]) > sum) {
                visited[i] = true;
                rowOrder[r] = i;
                if (transformToDominant(r + 1, visited, rowOrder)) {
                    return true;
                }
                visited[i] = false;
            }
        }
        return false;
    }

    public double[] solve(double[][] coefficients, double[] constants){
         return solve(coefficients, constants, this.tolerance);
    }

    public double[] solve(double[][] coefficients, double[] constants, double tolerance) {
        this.coefficients = deepCopyMatrix(coefficients);
        this.constants = Arrays.copyOf(constants, constants.length);
        this.solutionSteps.clear();
        this.answers.clear();
        this.iterationValues.clear();
        setTolerance(tolerance);

        printSystem();
        // makeDiagonallyDominant();
        diagonallyDominant();

        solutionSteps.add("\nStarting Gauss-Seidel Iteration:");
        solutionSteps.add("Using tolerance: " + formatNumber(tolerance));

        int n = this.coefficients.length;
        double[] current = new double[n];
        double[] previous = new double[n];
        Arrays.fill(current, 0);

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            System.arraycopy(current, 0, previous, 0, n);
            solutionSteps.add("\nIteration " + (iteration + 1) + ":");

            for (int i = 0; i < n; i++) {
                double sum = this.constants[i];
                StringBuilder formula = new StringBuilder("x" + (i + 1) + " = (" +
                        formatNumber(this.constants[i]) + " - (");

                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum -= this.coefficients[i][j] * current[j];
                        formula.append(formatNumber(this.coefficients[i][j]))
                                .append("*x").append(j + 1)
                                .append(" [").append(formatNumber(current[j])).append("]");

                        if (j < n - 1 && j != i - 1) {
                            formula.append(" + ");
                        }
                    }
                }

                formula.append(")) / ").append(formatNumber(this.coefficients[i][i]));
                current[i] = sum / this.coefficients[i][i];
                formula.append(" = ").append(formatNumber(current[i]));
                solutionSteps.add(formula.toString());
            }

            iterationValues.add(Arrays.copyOf(current, current.length));
            solutionSteps.add(formatIteration(iteration, current));

            if (hasConverged(current, previous)) {
                answers.add("Solution converged after " + (iteration + 1) + " iterations");
                answers.add("Final tolerance: " + formatNumber(tolerance));

                answers.add("\nFinal Solution:");
                for (int i = 0; i < current.length; i++) {
                    answers.add("x" + (i + 1) + " = " + formatNumber(current[i]));
                }
                
                return current;
            }
        }

        answers.add("Warning: Maximum iterations (" + MAX_ITERATIONS + ") reached without convergence");
        return current;
    }

    private boolean hasConverged(double[] current, double[] previous) {
        for (int i = 0; i < current.length; i++) {
            if (Math.abs(current[i] - previous[i]) > tolerance) {
                return false;
            }
        }
        return true;
    }

    private String formatIteration(int iteration, double[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Iteration %2d: [", iteration + 1));
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatNumber(values[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    public void printSolution() {
        System.out.println("\nGauss-Seidel Method Solution");
        System.out.println("Tolerance: " + formatNumber(tolerance));
        System.out.println("Maximum iterations: " + MAX_ITERATIONS);

        for (String step : solutionSteps) {
            System.out.println(step);
        }

        System.out.println("\nResults:");
        for (String answer : answers) {
            System.out.println(answer);
        }

        if (!iterationValues.isEmpty()) {
            double[] solution = iterationValues.get(iterationValues.size() - 1);
            System.out.println("\nFinal Solution:");
            for (int i = 0; i < solution.length; i++) {
                System.out.printf("x%d = %s\n", i + 1, formatNumber(solution[i]));
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of equations: ");
        int n = scanner.nextInt();

        double[][] coefficients = new double[n][n];
        double[] constants = new double[n];

        System.out.println("Enter coefficient matrix (row-wise):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                coefficients[i][j] = scanner.nextDouble();
            }
        }

        System.out.println("Enter constants vector:");
        for (int i = 0; i < n; i++) {
            constants[i] = scanner.nextDouble();
        }

        System.out.print("Enter tolerance (default 0.0001): ");
        double tolerance = scanner.hasNextDouble() ? scanner.nextDouble() : 0.0001;

        Gauss_Seidel solver = new Gauss_Seidel();
        solver.solve(coefficients, constants, tolerance);
        solver.printSolution();

        scanner.close();
    }
}
