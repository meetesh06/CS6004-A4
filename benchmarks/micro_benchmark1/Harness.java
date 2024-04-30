public class Harness {

    static class ParentFormula {
        double pie = Math.PI;
    }

    static abstract class Shape extends ParentFormula {
        abstract double area();
    }

    static class Circle extends Shape {
        double radius;

        Circle(double radius) {
            this.radius = radius;
        }

        double circleAreaRes() { return pie * radius * radius; }

        @Override
        double area() {
            return circleAreaRes();
        }
    }

    static class Square extends Shape {
        double side;

        Square(double side) {
            this.side = side;
        }

        double squareAreaRes() { return side * side; }

        @Override
        double area() {
            return squareAreaRes();
        }
    }

    static class Rectangle extends Shape {
        double length, width;

        Rectangle(double l, double w) {
            this.length = l;
            this.length = w;
        }

        double rectAreaRes() { return length * width; }

        @Override
        double area() {
            return rectAreaRes();
        }
    }

    public static void main(String[] args) {
        int iterations = 1000; // Number of iterations for the benchmark
        
        for (int i = 0; i < iterations; i++) {
            double area = 0;
            if (i % 2 == 0) {
                Shape r = new Rectangle(iterations, i / 2);
                for (int j = 0; j < iterations; j++) {
                    area = r.area();
                }
            } else {
                Shape c = new Circle(i);
                for (int k = 0; k < iterations; k++) {
                    area = c.area();
                }
            }
        }
    }
}
