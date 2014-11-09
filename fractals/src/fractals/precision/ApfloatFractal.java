package fractals.precision;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import fractals.Fractal;
import fractals.parallel.Point;
import fractals.parallel.RenderTask;

public class ApfloatFractal implements Fractal{
	private static final int precision =100; 
	
	protected static int threads = Runtime.getRuntime().availableProcessors()-1;
	protected ExecutorService e = Executors.newFixedThreadPool(threads);
	protected ApfloatRenderTask[] tasks;
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
		tasks = new ApfloatRenderTask[threads];
		createTasks();
	}
	
	public void setBounds(ApfloatPoint b1, ApfloatPoint b2){
		this.p1 = b1;
		this.p2 = b2;
		valid = false;
	}
	
	// split into columns
	private void createTasks(){
		Apfloat apthreads = new Apfloat(threads, precision);
		Apfloat dx = ApfloatMath.abs(apthreads.divide(p1.x.subtract(p2.x)));
		//double dx = Math.abs(p1.x-p2.x)/threads;
		
		int twidth = width/threads;
		int theight = height/threads;
		//Point tsize = new Point((int)(size.x/threads), size.y);
		for(int t = 0; t < threads-1; t++){
			Apfloat tt = new Apfloat(t, precision);
			Apfloat ttt = new Apfloat(t+1, precision);
			ApfloatPoint b1 = new ApfloatPoint(p1.x.add(dx.multiply(tt)), p1.y); 
			ApfloatPoint b2 = new ApfloatPoint(p1.x.add(dx.multiply(ttt)), p2.y); 
			tasks[t] = new ApfloatRenderTask(
					b1, b2, twidth,theight, iterations);
		}
		Apfloat tt = new Apfloat(threads - 1, precision);
		ApfloatPoint b1 = new ApfloatPoint(p1.x.add(dx.multiply(tt)), p1.y); 
		ApfloatPoint b2 = new ApfloatPoint(p2.x, p2.y); 
		tasks[threads-1] =  new ApfloatRenderTask( 
				b1, b2, twidth,theight, iterations);
	}
	
	// prepares tasks for re-render
	private void updateTasks(){
		Apfloat apthreads = new Apfloat(threads, precision);
		Apfloat dx = ApfloatMath.abs(apthreads.divide(p1.x.subtract(p2.x)));
		
		int twidth = width/threads;
		int theight = height/threads;
		for(int t = 0; t < threads-1; t++){
			Apfloat tt = new Apfloat(t, precision);
			Apfloat ttt = new Apfloat(t+1, precision);
			ApfloatPoint b1 = new ApfloatPoint(p1.x.add(dx.multiply(tt)), p1.y); 
			ApfloatPoint b2 = new ApfloatPoint(p1.x.add(dx.multiply(ttt)), p2.y); 
			tasks[t].updateBounds(b1, b2);
		}
		Apfloat tt = new Apfloat(threads - 1, precision);
		ApfloatPoint b1 = new ApfloatPoint(p1.x.add(dx.multiply(tt)), p1.y); 
		ApfloatPoint b2 = new ApfloatPoint(p2.x, p2.y); 
		tasks[threads-1].updateBounds(b1, b2);
		xoffset = twidth;
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

	private void copyImage(ApfloatRenderTask task, int n) {
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

	public ApfloatPoint[] getBounds() {
		return new ApfloatPoint[] {p1,p2};
	}

	public void run() {
		render();
	}
	
}
