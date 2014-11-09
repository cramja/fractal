package fractals.precision;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import fractals.serial.SFractal;

public class ApfloatRenderTask implements Runnable {
	private static final int precision = 100;

	private int iterations;
	private double d;
	private double[][] reals, imags;
	private int[][] image;
	private ApfloatPoint p1, p2, size;
	private int width, height;

	public ApfloatRenderTask(ApfloatPoint p1, ApfloatPoint p2, int height,
			int width, int iterations) {
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
		Apfloat fwidth = new Apfloat(width, precision);
		Apfloat distx = ApfloatMath.abs(p1.x.subtract(p2.x));
		Apfloat dx = distx.divide(fwidth);
		
		Apfloat fheight = new Apfloat(height, precision);
		Apfloat disty = ApfloatMath.abs(p1.y.subtract(p2.y));
		Apfloat dy = disty.divide(fheight);

		Apcomplex[][] c = new Apcomplex[width][height];
		// calculate c values:
		Apfloat r = p1.x;
		for (int y = 0; y < height; y++) {
			Apfloat i = ApfloatMath.product(dy, new Apfloat(y, precision)).add(p1.y);
			c[0][y] = new Apcomplex(r, i);
		}
		Apfloat i = p1.y;
		for (int x = 0; x < width; x++) {
			r = ApfloatMath.product(dx, new Apfloat(x, precision)).add(p1.x);
			c[x][0] = new Apcomplex(r, i);
		}
		for (int x = 1; x < width; x++) {
			r = c[x][0].real();
			for (int y = 1; y < height; y++) {
				i = c[0][y].imag();
				c[x][y] = new Apcomplex(r,i);
			}
		}
		Apcomplex[][] v = new Apcomplex[width][height];
		Apfloat two = new Apfloat(2,precision);
		for (int iter = 0; iter < iterations; iter++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if(iter == 0){
						v[x][y] = new Apfloat(0,precision);
					}
					if(v[x][y] != null){
						v[x][y] = ApcomplexMath.pow(v[x][y], two).add(c[x][y]);
						Apfloat mag = ApcomplexMath.norm(v[x][y]);
						if (mag.longValue() > 4.0) {
							image[x][y] = iter;
							v[x][y] = null;
						}
					}
				}
			}
		}
		setColors();
	}

	private void setColors() {
		int m = 0x0000ff / iterations;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image[x][y] = image[x][y] * m;
			}
		}
	}

	public int[][] getImage() {
		return image;
	}

}
