package fractals.parallel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FractalCalculator {

    protected int threads = 1;
    protected ExecutorService e;
    protected RenderTask[] tasks;
    protected Future<?>[] futures;

    private static final int iterations = 50; // kept small for speed
    private Point p1, p2, size;
    private int[][] image;
    private int xoffset = 0;

    private boolean valid = false;

    public FractalCalculator(double r1, double i1, double r2, double i2,
                             int width, int height) {
        update(r1, i1, r2, i2, width, height);
        threads = Runtime.getRuntime().availableProcessors();
        e = Executors.newFixedThreadPool(threads);

        futures = new Future<?>[threads];
        tasks = new RenderTask[threads];
        createTasks();
        valid = false;
    }

    public void update(double r1, double i1, double r2, double i2,
                       int width, int height){
        this.p1 = new Point(r1, i1);
        this.p2 = new Point(r2, i2);
        this.size = new Point(width, height);
        image = new int[width][height];
        valid = false;
    }

    public void setBounds(Point b1, Point b2) {
        this.p1 = b1;
        this.p2 = b2;
        valid = false;
    }

    // split into columns
    private void createTasks() {
        double dx = Math.abs(p1.x - p2.x) / ((double) threads);
        Point tsize = new Point((int) (size.x / threads), size.y);
        for (int t = 0; t < threads - 1; t++) {
            tasks[t] = new RenderTask(
                    new Point(p1.x + dx * ((double) t), p1.y),
                    new Point(p1.x + dx * ((double) (t + 1)), p2.y),
                    tsize,
                    iterations);
        }
        tasks[threads - 1] = new RenderTask(
                new Point(p1.x + dx * ((double) (threads - 1)), p1.y),
                new Point(p2.x, p2.y),
                new Point((int) (size.x - (tsize.x * ((double) threads - 1))), size.y),
                iterations);
    }

    // prepares tasks for re-render
    private void updateTasks() {
        double dx = Math.abs(p1.x - p2.x) / threads;
        Point tsize = new Point((int) (size.x / threads), size.y);
        for (double t = 0; t < threads - 1; t++) {
            tasks[(int) t].updateBounds(new Point(p1.x + dx * t, p1.y),
                    new Point(p1.x + dx * (t + 1), p2.y));
        }
        tasks[threads - 1].updateBounds(new Point(p1.x + dx
                * ((double) (threads - 1)), p1.y), new Point(p2.x, p2.y));
        xoffset = (int) tsize.x;
    }

    public void render() {
        if (valid)
            return;
        updateTasks();
        for (int t = 0; t < threads; t++) {
            futures[t] = e.submit(tasks[t]);
        }
        for (int t = 0; t < threads; t++) {
            try {
                futures[t].get();
                copyImage(tasks[t], t);
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Failed to render fractal: "
                        + this.toString());
                e.printStackTrace();
            }
        }
        valid = true;
    }

    private void copyImage(RenderTask task, int n) {
        int[][] subImage = task.getImage();
        int xo = n * xoffset;
        for (int x = 0; x < subImage.length; x++) {
            for (int y = 0; y < subImage[0].length; y++) {
                image[xo + x][y] = subImage[x][y];
            }
        }
    }

    public int[][] getImage() {
        render();
        return image;
    }

    public Point[] getBounds() {
        return new Point[] { p1, p2 };
    }
}