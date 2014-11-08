package fractals.serial;

import fractals.Fractal;

public class SFractal implements Fractal{
	protected double xcenter= 0;
	protected double ycenter = 0;
	protected double zoom = 2;

	public int width = 50;
	public int height = 50;
	protected double[][] reals;
	protected double[][] imags;

	public final int iterations = 50;

	public int[][] image;

	protected boolean invalid = true;
	
	public SFractal() {
		image = new int[width][height];
		setZoom(1);
		setCenter(0,0);
	}

	public int[][] getImage(){
		if(invalid) render();
		return image;
	}
	
	protected void render() {
		image = new int[width][height];
		reals = new double[width][height];
		imags = new double[width][height];
		for (int i = 0; i < iterations; i++) {
			for (int x = 0; x < width; x++) {
				double cr = (zoom*2 / width)*x + (xcenter - zoom);
				for (int y = 0; y < height; y++) {
					double ci = (zoom*2 / height)*y + (ycenter - zoom);
					double rt = reals[x][y] * reals[x][y] - imags[x][y]
							* imags[x][y];
					double it = 2 * (imags[x][y] * reals[x][y]);
					reals[x][y] = rt + cr;
					imags[x][y] = it + ci;
					if (magnitude(reals[x][y], imags[x][y]) > 2
							&& image[x][y] == 0) {
						image[x][y] = i;
					}
				}
			}
		}
		setColors();
		invalid = false;
	}
	
	public void setCenter(double x, double y) {
		this.xcenter = x;
		this.ycenter = y;
		invalid = true;
	}

	public void setZoom(double z) {
		zoom = z;
		invalid = true;
	}

	public int getPixel(int i) {
		int x = i % width;
		int y = i / height;
		return image[x][y];
	}

	protected void setColors() {
		int m = 0x0000ff/iterations;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image[x][y] = image[x][y]*m;
			}
		}
	}

	public static double magnitude(double r, double i) {
		return Math.sqrt(Math.pow(r, 2) + Math.pow(i, 2));
	}

	public double getZoom() {
		return zoom;
	}
	
	public double getXcenter() {
		return xcenter;
	}

	public double getYcenter() {
		return ycenter;
	}

}
