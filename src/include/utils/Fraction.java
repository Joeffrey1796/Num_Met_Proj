package include.utils;

public class Fraction {
     //? Fraction class to handle rational numbers

        private int numerator;
        private int denominator;
        
        public Fraction(int numerator, int denominator) {
            if (denominator == 0) {
                throw new IllegalArgumentException("Denominator cannot be zero.");
            }
            //? Simplify the fraction
            int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
            this.numerator = numerator / gcd;
            this.denominator = denominator / gcd;
            if (this.denominator < 0) {
                this.numerator *= -1;
                this.denominator *= -1;
            }
        }
        
        public Fraction(double value) {
            this((int)(value * 100000), 100000);
        }
        
        private int gcd(int a, int b) {
            //* Helper method to calculate the greatest common divisor
            //? Using Euclidean algorithm
            
            return b == 0 ? a : gcd(b, a % b);
        }
        
        public Fraction add(Fraction other) {
            //* Add two fractions
            //? a/b + c/d = (ad + bc) / bd 

            int newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
            int newDenominator = this.denominator * other.denominator;
            return new Fraction(newNumerator, newDenominator);
        }
        
        public Fraction subtract(Fraction other) {
            //* Subtract two fractions
            //? a/b - c/d = (ad - bc) / bd

            int newNumerator = this.numerator * other.denominator - other.numerator * this.denominator;
            int newDenominator = this.denominator * other.denominator;
            return new Fraction(newNumerator, newDenominator);
        }
        
        public Fraction multiply(Fraction other) {
            //* Multiply two fractions
            //? a/b * c/d = (ac) / (bd)

            int newNumerator = this.numerator * other.numerator;
            int newDenominator = this.denominator * other.denominator;
            return new Fraction(newNumerator, newDenominator);
        }
        
        public Fraction divide(Fraction other) {
            //* Divide two fractions
            //? a/b / c/d = (ad) / (bc)

            if (other.numerator == 0) {
                throw new ArithmeticException("Cannot divide by zero");
            }
            int newNumerator = this.numerator * other.denominator;
            int newDenominator = this.denominator * other.numerator;
            return new Fraction(newNumerator, newDenominator);
        }
        
        public double doubleValue() {
            //* Convert the fraction to a double value
            
            return (double) numerator / denominator;
        }
        
        @Override
        public String toString() {
            //? Convert the fraction to a string for solution display

            if (denominator == 1) {
                return String.valueOf(numerator);
            }
            return numerator + "/" + denominator;
        }
}
