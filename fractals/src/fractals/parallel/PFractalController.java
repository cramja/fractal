package fractals.parallel;
import fractals.Direction;
import fractals.FractalController;

public class PFractalController implements FractalController{
	private PFractal fractal;
	private static final double zoomFactor = 0.05;
	private static final int f_width = 50;
	private static final int f_height = f_width;
	
	
	public PFractalController(){
		fractal = new PFractal(new Point(-2,-2), new Point(2,2), f_width, f_height);
	}
	
	public int[][] getImage(){
		if(h_thread != null && !h_thread.isAlive() && rendering && valid_hres)
			return h_fractal.getImage();
		return fractal.getImage();
	}
	
	private boolean rendering = false;
	private boolean valid_hres = false;
	private PFractal h_fractal;
	private Thread h_thread;
	
	public void renderHiRes(int width, int height){
		System.out.println("hi_res");
		if(rendering && !valid_hres && h_thread != null && h_thread.isAlive()) h_thread.interrupt();
		h_fractal = new PFractal(fractal.getBounds()[0], fractal.getBounds()[1], width, height);
		h_thread = new Thread(h_fractal);
		h_thread.start();
		rendering = true;
		valid_hres = true;
	}
	
	public void move(Direction direction){
		Point[] pts = fractal.getBounds();
		double w = Math.abs(pts[0].x-pts[1].x)/50;
		switch(direction){
			case UP:
				pts[0].y += w;
				pts[1].y += w;
				break;
			case DOWN:
				pts[0].y -= w;
				pts[1].y -= w;
				break;
			case LEFT:
				pts[0].x += w;
				pts[1].x += w;
				break;
			case RIGHT:
				pts[0].x -= w;
				pts[1].x -= w;
				break;
			default:
				break;
		}
		fractal.setBounds(pts[0], pts[1]);
		valid_hres = false;
	}
	
	public void increaseZoom() {
		Point[] pts = fractal.getBounds();
		double w = Math.abs(pts[0].x-pts[1].x)/50;
		pts[0].x += w;
		pts[1].x -= w;
		pts[0].y += w;
		pts[1].y -= w;
		fractal.setBounds(pts[0], pts[1]);
		System.out.println("bounds: " + pts[0] + " - " + pts[1]);
		valid_hres = false;
	}

	public void decreaseZoom() {
		Point[] pts = fractal.getBounds();
		double w = Math.abs(pts[0].x-pts[1].x)/50;
		pts[0].x -= w;
		pts[1].x += w;
		pts[0].y -= w;
		pts[1].y += w;
		fractal.setBounds(pts[0], pts[1]);
		System.out.println("bounds: " + pts[0] + " - " + pts[1]);
		valid_hres = false;
	}
}
