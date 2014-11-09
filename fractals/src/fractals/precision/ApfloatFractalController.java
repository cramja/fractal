package fractals.precision;
import org.apfloat.Apfloat;

import fractals.Direction;
import fractals.FractalController;

public class ApfloatFractalController implements FractalController {
	private static final int precision = 100;
	
	
	private ApfloatFractal fractal = new ApfloatFractal(
			new ApfloatPoint(new Apfloat(-2,precision), new Apfloat(-2, precision)),
			new ApfloatPoint(new Apfloat(2,precision), new Apfloat(2, precision)), 50, 50);
	
	@Override
	public int[][] getImage() {
		return fractal.getImage();
	}

	@Override
	public void move(Direction direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseZoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseZoom() {
		// TODO Auto-generated method stub
		
	}
}
