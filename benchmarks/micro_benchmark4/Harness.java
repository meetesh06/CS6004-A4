class FibonacciCalculator {

    // Calculate Fibonacci using iterative approach
    public int iterativeFibonacci(int n) {
        if (n <= 1) {
            return n;
        }

        int fib = 1;
        int prevFib = 1;

        for (int i = 2; i < n; i++) {
            int temp = fib;
            fib += prevFib;
            prevFib = temp;
        }

        return fib;
    }

    // Calculate Fibonacci using recursive approach
    public int recursiveFibonacci(int n) {
        if (n <= 1) {
            return n;
        }

        return recursiveFibonacci(n - 1) + recursiveFibonacci(n - 2);
    }
}

public class Harness {

    public static void main(String[] args) {
        FibonacciCalculator calculator = new FibonacciCalculator();
        int n = 25;

        for (int i = 0; i < n; i++) {
            calculator.iterativeFibonacci(i);
        }

        for (int i = 0; i < n; i++) {
            calculator.recursiveFibonacci(i);
        }
    }
}
