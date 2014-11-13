package fractals.parallel;

public class Point {
    public double x, y;

    public Point(double xx, double yy) {
        x = xx;
        y = yy;
    }

    public String toString() {
        return String.format("%.8f, %.8f", x, y);
    }

    public Point clone() {
        return new Point(this.x, this.y);
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
