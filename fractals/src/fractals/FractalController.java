package fractals;


public interface FractalController {

	int[][] getImage();

	public void move(Direction direction);

	void increaseZoom();

	void decreaseZoom();

}
