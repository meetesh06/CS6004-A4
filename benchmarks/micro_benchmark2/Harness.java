class A {
    int foo(int a, int b) {
        return a + b;
    }
}

class B extends A {
    int foo(int a, int b) {
        return a + b;
    }
}

public class Harness {
    public static void main(String[] args) {
        A a = new A();
        int iterations = 100000000;
        int res = 0;
        for (int i = 0; i < iterations; i++) {
            res = a.foo(i, i+1);
        }
    }
}
