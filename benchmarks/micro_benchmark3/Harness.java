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

class C extends B {
    int foo(int a, int b) {
        return a + b;
    }
}

class D extends C {
    int foo(int a, int b) {
        return a + b;
    }
}

class E extends D {
    int foo(int a, int b) {
        return a + b;
    }
}

class F extends E {
    int foo(int a, int b) {
        return a + b;
    }
}

class G extends F {
    int foo(int a, int b) {
        return a + b;
    }
}

class H extends G {
    int foo(int a, int b) {
        return a + b;
    }
}

public class Harness {
    public static void main(String[] args) {
        A a = new G();
        int iterations = 100000000;
        int res = 0;
        for (int i = 0; i < iterations; i++) {
            res = a.foo(i, i+1);
        }
    }
}
