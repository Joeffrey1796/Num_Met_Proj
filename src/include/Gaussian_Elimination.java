package include;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import include.utils.Fraction;


// Note: AI genereated documentation hehehe
public class Gaussian_Elimination {
    
    private List<String> msgSoln;  // List to store solution steps
    private List<String> answers;  // List to store final answers
    
    public static void main(String[] args) {
        //TODO: Example usage
        double[][] matrixA = {
            {2, -1, -3},
            {1, 4, -2},
            {3, 1, 5}
        };
        double[] matrixB = {5, 1, 2};
        
        Gaussian_Elimination solver = new Gaussian_Elimination();
        boolean success = solver.solve(matrixA, matrixB);
        solver.sysoutSoln(success, solver);

        System.out.println("\n\n\n\n\n");
        boolean success2 = solver.solve(matrixA, new double[]{5, 1,8});
        solver.sysoutSoln(success2, solver);
    }
      
    
    public Gaussian_Elimination() {
        /*  
         * Constructor to initialize the lists for solution steps and answers
         */
        this.msgSoln = new ArrayList<>();
        this.answers = new ArrayList<>();
    }
    
    public List<String> getSolutionSteps() {
        /*
         * Method to retrieve the solution steps
         * @return List of strings representing the solution steps
         */
        return msgSoln;
    }
    
    public List<String> getAnswers() {
        /*
         * Method to retrieve the final answers
         * @return List of strings representing the final answers
         */
        return answers;
    }
    
    private boolean isSquareMatrix(double[][] matrix) {
        /*
         * Method to check if the matrix is square
         * @param matrix The matrix to check
         * @return true if the matrix is square, false otherwise
         */
        if (matrix.length == 0) return false;
        int rows = matrix.length;
        for (double[] row : matrix) {
            if (row.length != rows) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isCompatibleMatrix(double[][] matrixA, double[] matrixB) {
        /*
         * Method to check if the constant matrix is compatible with the coefficient matrix
         * @param matrixA The coefficient matrix
         * @param matrixB The constant matrix
         * @return true if the matrices are compatible, false otherwise
         */
        return matrixB.length == matrixA.length;
    }
    
    private void backwardsSubstitution(Fraction[][] matrix, int size) {
        /*
         * Method to perform backward substitution to find the solution
         * @param matrix The augmented matrix after forward elimination
         * @param size The size of the matrix
         * @return void
         */

        Fraction[] x = new Fraction[size];
        DecimalFormat df = new DecimalFormat("0.00##"); 
        
        x[size - 1] = matrix[size - 1][size].divide(matrix[size - 1][size - 1]);
        
        for (int i = size - 2; i >= 0; i--) {
            Fraction sum = matrix[i][size];
            for (int j = i + 1; j < size; j++) {
                sum = sum.subtract(matrix[i][j].multiply(x[j]));
            }
            x[i] = sum.divide(matrix[i][i]);
        }
        
        //? Store answers in both decimal and fraction form in Gaussian_Elimination class
        answers.add("\nDecimal Form");
        for (int i = 0; i < x.length; i++) {
            answers.add("var " + (i + 1) + ": " + df.format(x[i].doubleValue()));
        }
        
        answers.add("\nFraction Form");
        for (int i = 0; i < x.length; i++) {
            answers.add("x_" + (i + 1) + ": " + x[i].toString());
        }
        
    }
    
    private Fraction[][] forwardElimination(double[][] matrixA, double[] matrixB) {
        /*
         * Method to perform forward elimination on the augmented matrix
         * @param matrixA The coefficient matrix
         * @param matrixB The constant matrix
         * @return The augmented matrix after forward elimination in Fraction form
         */

        int n = matrixA.length;
        Fraction[][] augmentedMatrix = new Fraction[n][n + 1];
        
        //? Initialize augmented matrix with Fractions
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmentedMatrix[i][j] = new Fraction(matrixA[i][j]);
            }
            augmentedMatrix[i][n] = new Fraction(matrixB[i]);
        }
        
        msgSoln.add("Augmented Matrix (Initial): ");
        msgSoln.add(matrixToString(augmentedMatrix));
        msgSoln.add(" ");
        
        //? Partial pivoting
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            Fraction maxVal = augmentedMatrix[i][i];
            for (int k = i + 1; k < n; k++) {
                if (augmentedMatrix[k][i].doubleValue() > maxVal.doubleValue()) {
                    maxVal = augmentedMatrix[k][i];
                    maxRow = k;
                }
            }
            
            if (maxRow != i) {
                Fraction[] temp = augmentedMatrix[i];
                augmentedMatrix[i] = augmentedMatrix[maxRow];
                augmentedMatrix[maxRow] = temp;
                
                msgSoln.add("Swapped rows " + i + " and " + maxRow + ":");
                msgSoln.add(matrixToString(augmentedMatrix));
                msgSoln.add(" ");
            }
        }
        
        msgSoln.add("Solution: ");
        
        //? Forward elimination
        for (int i = 0; i < n; i++) {
            if (augmentedMatrix[i][i].doubleValue() == 0.0) {
                msgSoln.add("0 division Error");
                return null;
            }
            
            for (int j = i + 1; j < n; j++) {
                Fraction scalingFactor = augmentedMatrix[j][i].divide(augmentedMatrix[i][i]);
                
                for (int k = i; k < n + 1; k++) {
                    augmentedMatrix[j][k] = augmentedMatrix[j][k].subtract(
                        scalingFactor.multiply(augmentedMatrix[i][k]));
                }
                
                msgSoln.add("Row " + (j + 1) + " updated by subtracting " + scalingFactor + " * Row " + (i + 1) + ":");
                msgSoln.add(matrixToString(augmentedMatrix));
                msgSoln.add(" ");
            }
        }
        
        return augmentedMatrix;
    }
    
    private String matrixToString(Fraction[][] matrix) {
        /*
         * Method to convert the matrix to a string representation
         * @param matrix The matrix to convert
         * @return String representation of the matrix
         */
        StringBuilder sb = new StringBuilder();
        for (Fraction[] row : matrix) {
            for (Fraction entry : row) {
                sb.append(entry.toString()).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public boolean solve(double[][] matrixA, double[] matrixB) {
        /*
         * Method to solve the system of equations using Gaussian elimination
         * @param matrixA The coefficient matrix
         * @param matrixB The constant matrix
         * @return true if the solution is successful, false otherwise
         */
        msgSoln.clear();
        answers.clear();
        
        //* Validate matrices
        if (!isSquareMatrix(matrixA)) {
            msgSoln.add("Error: Coefficient matrix is not square.");
            return false;
        }
        
        if (!isCompatibleMatrix(matrixA, matrixB)) {
            msgSoln.add("Error: Constant matrix is not compatible with coefficient matrix.");
            return false;
        }
        
        Fraction[][] augMatrix = forwardElimination(matrixA, matrixB);
        
        if (augMatrix == null) {
            return false;
        }
        
        backwardsSubstitution(augMatrix, augMatrix[0].length - 1);
        return true;
    }
    
    public void sysoutSoln(boolean success, Gaussian_Elimination solver) {
        /*
         * Method to print the solution steps and final answers
         * @param success Indicates if the solution was successful
         */
        if (success) {
            System.out.println("Solution Steps:");
            for (String step : solver.getSolutionSteps()) {
                System.out.println(step);
            }
            
            System.out.println("\nFinal Answers:");
            for (String answer : solver.getAnswers()) {
                System.out.println(answer);
            }
        } else {
            System.out.println("Solution failed:");
            for (String error : solver.getSolutionSteps()) {
                System.out.println(error);
            }
        }
    }
      
}