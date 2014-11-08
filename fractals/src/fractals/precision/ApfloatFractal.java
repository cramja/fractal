package fractals.precision;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fractal.Fractal;
import fractal.parallel.Point;
import fractal.parallel.RenderTask;

public class ApfloatFractal implements Fractal{
	protected static int threads = Runtime.getRuntime().availableProcessors()-1;
	protected ExecutorService e = Executors.newFixedThreadPool(threads);
	protected RenderTask[] tasks;
	protected Future<?>[] futures;
	
	private static final int iterations = 50;
	private ApfloatPoint p1, p2;
	private int width, height;
	private int[][] image;
	private int xoffset = 0;
	
	private boolean valid = false;
	
	public ApfloatFractal(ApfloatPoint p1, ApfloatPoint p2, int width, int height){
		this.p1 = p1;
		this.p2 = p2;
		this.width = width;
		this.height = height;
		image = new int[width][height];
		
		futures = new Future<?>[threads];
		tasks = new RenderTask[threads];
		createTasks();
	}
	
	public void setBounds(ApfloatPoint b1, ApfloatPoint b2){
		this.p1 = b1;
		this.p2 = b2;
		valid = false;
	}
	
	// split into columns
	private void createTasks(){
		double dx = Math.abs(p1.x-p2.x)/threads;
		Point tsize = new Point((int)(size.x/threads), size.y);
		for(int t = 0; t < threads-1; t++){
			tasks[t] = new RenderTask( 
					new Point(p1.x + dx*t,p1.y), 
					new Point(p1.x + dx*(t+1), p2.y), 
					tsize, iterations);
		}
		tasks[threads-1] = new RenderTask(
				new Point(p1.x + dx * (threads-1), p1.y), 
				new Point(p2.x, p2.y),new Point((int) (size.x-(tsize.x*(threads-1))), size.y),iterations);
	}
	
	// prepares tasks for re-render
	private void updateTasks(){
		double dx = Math.abs(p1.x-p2.x)/threads;
		Point tsize = new Point((int)(size.x/threads), size.y);
		for(int t = 0; t < threads-1; t++){
			tasks[t].updateBounds(new Point(p1.x + dx*t,p1.y), 
					new Point(p1.x + dx*(t+1), p2.y));
		}
		tasks[threads-1].updateBounds(new Point(p1.x + dx * (threads-1), p1.y), 
				new Point(p2.x, p2.y));
		xoffset = (int) tsize.x;
	}
	
	public void render(){
		if(valid) return;
		updateTasks();
		for(int t = 0; t < threads; t++){
			futures[t] = e.submit(tasks[t]);
		}
		for(int t = 0; t < threads; t++){
			try {
				futures[t].get();
				copyImage(tasks[t],t);
			} catch (InterruptedException | ExecutionException e) {
				System.err.println("Failed to render fractal: " + this.toString());
				e.printStackTrace();
			}
		}
		valid = true;
	}

	private void copyImage(RenderTask task, int n) {
		int[][] subImage = task.getImage();
		int xo = n*xoffset;
		for(int x = 0; x < subImage.length; x++){
			for(int y = 0; y < subImage[0].length; y++){
				image[xo + x][y] = subImage[x][y];
			}
		}
	}

	public int[][] getImage() {
		render();
		return image;
	}

	public Point[] getBounds() {
		return new Point[] {p1,p2};
	}

	@Override
	public void run() {
		render();
	}
}
