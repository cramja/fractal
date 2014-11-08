package fractals.parallel;
import fractals.serial.SFractal;

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
	
	public void updateBounds(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
		this.image = new int[(int) size.x][(int) size.y];
	}

	@Override
	public void run() {
		double dx = Math.abs(p1.x - p2.x)/size.x;
		double dy = Math.abs(p1.y - p2.y)/size.y;
		reals = new double[(int) size.x][(int) size.y];
		imags = new double[(int) size.x][(int) size.y];
		for (int i = 0; i < iterations; i++) {
			for (int x = 0; x < size.x; x++) {
				double cr = dx*x + p1.x;
				for (int y = 0; y < size.y; y++) {
					double ci = dy*y + p1.y;
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
