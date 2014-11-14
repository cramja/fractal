package fractals.parallel;
import fractals.Direction;
import fractals.FractalController;
import fractals.remote.Master;

public class PFractalController implements FractalController{
    private Master workController;
	private static final double zoomFactor = 0.05;
	private static final int f_width = 200;
	private static final int f_height = f_width;
    private Point p1, p2;
	
	
	public PFractalController(){
        workController = new Master();
        setBounds(new Point(-2,-2), new Point(2,2));
        workController.updateBounds(getBounds());
        int numWorkers = workController.numWorkers();
        workController.initWorkers(numWorkers,  f_width, f_height);
	}
	
	public int[][] getImage(){
        workController.updateBounds(getBounds());
		return workController.getImage(f_width, f_height);
	}
	
	public void move(Direction direction){
		Point[] pts = getBounds();
		double w = Math.abs(pts[0].x-pts[1].x)/50;
		switch(direction){
			case UP:
                pts[0].y -= w;
                pts[1].y -= w;
				break;
			case DOWN:
                pts[0].y += w;
                pts[1].y += w;
				break;
			case LEFT:
                pts[0].x -= w;
                pts[1].x -= w;
				break;
			case RIGHT:
                pts[0].x += w;
                pts[1].x += w;
				break;
			default:
				break;
		}
		setBounds(pts[0], pts[1]);
	}
	
	public void increaseZoom() {
		Point[] pts = getBounds();
		double w = Math.abs(pts[0].x-pts[1].x)/50;
		pts[0].x += w;
		pts[1].x -= w;
		pts[0].y += w;
		pts[1].y -= w;
        setBounds(pts[0], pts[1]);
	}

	public void decreaseZoom() {
		Point[] pts = getBounds();
		double w = Math.abs(pts[0].x-pts[1].x)/50;
		pts[0].x -= w;
		pts[1].x += w;
		pts[0].y -= w;
		pts[1].y += w;
        setBounds(pts[0], pts[1]);
	}

    public Point[] getBounds() {
        return new Point[] { p1, p2 };
    }

    public void setBounds(Point b1, Point b2) {
        this.p1 = b1;
        this.p2 = b2;
    }
}
