package fractals.parallel;

public class RenderTask implements Runnable {

    private int iterations;
    private double d;
    private double[][] reals, imags;
    private int[][] image;
    private Point p1, p2, size;

    public RenderTask(Point p1, Point p2, Point size, int iterations) {
        this.iterations = iterations;
        this.p1 = p1;
        this.p2 = p2;
        this.size = size;
        this.image = new int[(int) size.x][(int) size.y];
    }

    public void updateBounds(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.image = new int[(int) size.x][(int) size.y];
    }

    // how many iterations until it diverges?
    public int singlePoint(Point complex) {
        Point tcomplex = complex.clone();
        for (int i = 0; i < iterations; i++) {
            tcomplex.x = tcomplex.x * tcomplex.x - tcomplex.y * tcomplex.y
                    + complex.x;
            tcomplex.y = tcomplex.y * tcomplex.x * 2 + complex.y;
            if (new Point(tcomplex.x, tcomplex.y).magnitude() > 2)
                return i;
        }
        return 0;
    }

    public void run() {
        double dx = Math.abs(p1.x - p2.x) / size.x;
        double dy = Math.abs(p1.y - p2.y) / size.y;
        reals = new double[(int) size.x][(int) size.y];
        imags = new double[(int) size.x][(int) size.y];
        for (int i = 0; i < iterations; i++) {
            for (int x = 0; x < size.x; x++) {
                double cr = dx * ((double) x) + p1.x;
                for (int y = 0; y < size.y; y++) {
                    if (image[x][y] != 0)
                        continue;
                    double ci = dy * ((double) y) + p1.y;
                    double rt = reals[x][y] * reals[x][y] - imags[x][y]
                            * imags[x][y];
                    double it = 2d * (imags[x][y] * reals[x][y]);
                    reals[x][y] = rt + cr;
                    imags[x][y] = it + ci;

                    if (new Point(reals[x][y], imags[x][y]).magnitude() > 2) {
                        image[x][y] = i;
                    }
                }
            }
        }
        setColors();
    }

    private void setColors() {
        float range = (float) (iterations >> 2);
        float r_decr = 0;
        float g_decr = range;
        float r_incr = range * 2;
        float b_decr = range * 3;
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                if (image[x][y] == 0)
                    continue;
                int rgb = 0xffffff;
                if (image[x][y] > r_decr && image[x][y] < g_decr) {
                    rgb -= ((image[x][y] % range) / range) * 255 * 0x010000;
                } else if (image[x][y] < r_incr && image[x][y] > r_decr) {
                    rgb -= ((image[x][y] % range) / range) * 255 * 0x000100;
                    rgb -= 0xff0000;
                } else if (image[x][y] < r_incr && image[x][y] > r_decr) {
                    rgb -= 0xff0000;
                    rgb -= 0x00ff00;
                    rgb += ((image[x][y] % range) / range) * 255 * 0x000100;
                } else {
                    rgb -= 0x00ff00;
                    rgb -= ((image[x][y] % range) / range) * 255 * 0x000001;
                }
                image[x][y] = rgb;
            }
        }
    }

    public int[][] getImage() {
        return image;
    }
}