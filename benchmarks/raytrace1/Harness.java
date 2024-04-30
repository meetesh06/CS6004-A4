import java.awt.Color;
import java.util.Random;

// Define a 3D vector class for calculations
class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 multiply(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    public double dotProduct(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 crossProduct(Vector3 other) {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalize() {
        double mag = magnitude();
        return new Vector3(x / mag, y / mag, z / mag);
    }
}

// Define a class for rays
class Ray {
    private Vector3 origin;
    private Vector3 direction;

    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Vector3 getOrigin() {
        return origin;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public Vector3 pointAtParameter(double t) {
        return origin.add(direction.multiply(t));
    }
}

// Define a class for objects in the scene
abstract class SceneObject {
    public abstract double intersect(Ray ray);
    public abstract Vector3 getCenter(); // Add method to get center
    public abstract Color getColor(); // Add method to get color
}
// Define a class for spheres as scene objects
class Sphere extends SceneObject {
    private Vector3 center;
    private double radius;
    private Color color;

    public Sphere(Vector3 center, double radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public double intersect(Ray ray) {
        Vector3 oc = ray.getOrigin().subtract(center);
        double a = ray.getDirection().dotProduct(ray.getDirection());
        double b = 2.0 * oc.dotProduct(ray.getDirection());
        double c = oc.dotProduct(oc) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return -1.0; // No intersection
        } else {
            return (-b - Math.sqrt(discriminant)) / (2.0 * a);
        }
    }

    @Override
    public Vector3 getCenter() {
        return center;
    }

    @Override
    public Color getColor() {
        return color;
    }
}


// Define a class for the scene
class Scene {
    private SceneObject[] objects;

    public Scene(SceneObject[] objects) {
        this.objects = objects;
    }

    public void traceRays(int numRays) {
        Vector3 cameraPosition = new Vector3(0, 0, 0);
        Vector3 lookAt = new Vector3(0, 0, -1);
        Vector3 cameraDirection = lookAt.subtract(cameraPosition).normalize();

        for (int i = 0; i < numRays; i++) {
            double u = Math.random();
            double v = Math.random();
            Ray ray = new Ray(cameraPosition, cameraDirection.add(new Vector3(u, v, 0)));
            trace(ray, 0); // Start with recursion depth 0
        }
    }

    private Color trace(Ray ray, int depth) {
        if (depth >= 5) { // Maximum recursion depth
            return Color.BLACK; // Return black to stop recursion
        }

        double closestIntersection = Double.POSITIVE_INFINITY;
        SceneObject closestObject = null;

        for (SceneObject object : objects) {
            double t = object.intersect(ray);
            if (t > 0 && t < closestIntersection) {
                closestIntersection = t;
                closestObject = object;
            }
        }

        if (closestObject != null) {
            // Recursive reflection for simplicity (you can add more effects)
            Vector3 hitPoint = ray.pointAtParameter(closestIntersection);
            Vector3 normal = hitPoint.subtract(closestObject.getCenter()).normalize();
            Ray reflectedRay = new Ray(hitPoint, ray.getDirection().subtract(normal.multiply(2 * ray.getDirection().dotProduct(normal))));
            Color reflectedColor = trace(reflectedRay, depth + 1);
            // Adjust color based on reflection (for demonstration purposes)
            return adjustColor(closestObject.getColor(), reflectedColor);
        } else {
            return Color.BLACK;
        }
    }

    private Color adjustColor(Color baseColor, Color reflectionColor) {
        // Adjust color based on reflection (for demonstration purposes)
        // Here, we simply blend the colors with a fixed ratio
        int red = (int) (baseColor.getRed() * 0.7 + reflectionColor.getRed() * 0.3);
        int green = (int) (baseColor.getGreen() * 0.7 + reflectionColor.getGreen() * 0.3);
        int blue = (int) (baseColor.getBlue() * 0.7 + reflectionColor.getBlue() * 0.3);
        return new Color(red, green, blue);
    }
}


// Define the Benchmark class for ray tracing computations
public class Harness {
    public static void main(String[] args) {
        // Create an array to hold scene objects
        SceneObject[] objects = new SceneObject[10]; // Adjust the number of objects as needed

        // Add random spheres to the scene
        Random random = new Random();
        for (int i = 0; i < objects.length; i++) {
            double x = random.nextDouble() * 20 - 10; // Random x position (-10 to 10)
            double y = random.nextDouble() * 20 - 10; // Random y position (-10 to 10)
            double z = random.nextDouble() * 20 - 10; // Random z position (-10 to 10)
            double radius = random.nextDouble() * 3 + 1; // Random radius (1 to 4)
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // Random color
            objects[i] = new Sphere(new Vector3(x, y, z), radius, color); // Create and add sphere to objects array
        }

        Scene scene = new Scene(objects); // Create the scene with random objects

        long startTime = System.nanoTime();

        scene.traceRays(100000); // Adjust the number of rays here (e.g., 10 million)

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convert to milliseconds

    }

}
