package fractals.parallel;
public class Point {
	public double x,y;
	
	public Point(double xx, double yy){
		x = xx; y = yy;
	}
	
	public String toString(){
		return String.format("%.8f, %.8f", x,y);
	}
}
