package fractals.precision;
import fractals.serial.SFractal;

public class ApfloatRenderTask implements Runnable {
	private int iterations;
	private double d;
	private double[][] reals, imags;
	private int[][] image;
	private ApfloatPoint p1, p2, size;
	private int width, height;

	public ApfloatRenderTask(ApfloatPoint p1, ApfloatPoint p2, int height, int width, int iterations) {
		this.iterations = iterations;
		this.p1 = p1;
		this.p2 = p2;
		this.size = size;
		this.height = height;
		this.width = width;
		this.image = new int[width][height];
	}

	public void updateBounds(ApfloatPoint p1, ApfloatPoint p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.image = new int[width][height];
	}

	@Override
	public void run() {
		//Apfloat dx = ApfloatMath.
		double dx = Math.abs(p1.x - p2.x) / size.x;
		double dy = Math.abs(p1.y - p2.y) / size.y;
		reals = new double[(int) size.x][(int) size.y];
		imags = new double[(int) size.x][(int) size.y];
		for (int i = 0; i < iterations; i++) {
			for (int x = 0; x < size.x; x++) {
				double cr = dx * x + p1.x;
				for (int y = 0; y < size.y; y++) {
					double ci = dy * y + p1.y;
					double rt = reals[x][y] * reals[x][y] - imags[x][y]
							* imags[x][y];
					double it = 2 * (imags[x][y] * reals[x][y]);
					reals[x][y] = rt + cr;
					imags[x][y] = it + ci;
					if (SFractal.magnitude(reals[x][y], imags[x][y]) > 2
							&& image[x][y] == 0) {
						image[x][y] = i;
					}
				}
			}
		}
		setColors();
	}

	private void setColors() {
		int m = 0x0000ff / iterations;
		for (int x = 0; x < size.x; x++) {
			for (int y = 0; y < size.y; y++) {
				image[x][y] = image[x][y] * m;
			}
		}
	}

	public int[][] getImage() {
		return image;
	}

}
