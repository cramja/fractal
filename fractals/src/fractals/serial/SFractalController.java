package fractals.serial;

import fractals.Direction;
import fractals.FractalController;

public class SFractalController implements FractalController{	
	private SFractal fractal;
	private static final double zoomFactor = 0.05;
	
	public SFractalController(){
		fractal = new SFractal();
	}
	
	public int[][] getImage(){
		return fractal.getImage();
	}
	
	public void move(Direction direction){
		double d = fractal.getZoom()/10; // TODO: magic number
		double x = fractal.getXcenter();
		double y = fractal.getYcenter();
		switch(direction){
			case UP:
				y += d;
				break;
			case DOWN:
				y -= d;
				break;
			case LEFT:
				x -= d;
				break;
			case RIGHT:
				x+= d;
				break;
			default:
				break;
		}
		fractal.setCenter(x,y);
	}
	
	public void increaseZoom() {
		fractal.setZoom(fractal.getZoom() + fractal.getZoom()*zoomFactor);
	}

	public void decreaseZoom() {
		fractal.setZoom(fractal.getZoom() - fractal.getZoom()*zoomFactor);
	}
}
