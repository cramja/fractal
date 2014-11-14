package fractals.udp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import fractals.Direction;
import fractals.FractalController;
import fractals.parallel.FractalCalculator;

public class Controller implements FractalController {

	double p1x = -1.5;
	double p1y = -1.5;
	double p2x = 1.5;
	double p2y = 1.5;
	int width = 20;
	int height = 20;

	FractalCalculator mFractal;
	Client mClient;
	
	int[][] img;

	public static void main(String[] args) {
		int port = 2000;
		if (args.length != 1) // Test for correct argument list
			System.out.println("Using port " + port);
		else
			port = Integer.parseInt(args[0]);
		
		Controller controller = new Controller(port);
		try {
			controller.test();
		} catch (IOException | InterruptedException | ExecutionException e) {
			System.out.println("Test failed " + e.getMessage());
		}
	}

	public Controller(int port) {
		mClient = new Client(port);
		try {
			mClient.addServer("10.2.6.103",2000);
//			mClient.addServer("10.2.6.103",2001);
//			mClient.addServer("",2002);
//			mClient.addServer("",2003);
		} catch (UnknownHostException e) {
			System.out.println("Couldn't find address.");
		}
		mFractal = new FractalCalculator(p1x, p1y, p2x, p2y, width, height);
		mFractal.render();
	}
	
	public void test() throws IOException, InterruptedException, ExecutionException{
		mClient.request(p1x, p1y, p2x, p2y, width, height);
		Thread.currentThread().sleep(1000); // wait one second
		int[] response = mClient.response();
		int[][] responsePic = new int[width][height];
		for(int i = 0; i < response.length;i++){
			responsePic[i%width][i/width] = response[i];
		}
		if(imageEqual(responsePic, mFractal.getImage()))
			System.out.println("Server worked correcly");
		else
			System.out.println("Server failed to return correctly");
	}
	
	public boolean imageEqual(int[][] i1, int[][] i2){
		if(i1.length != i2.length || i1[0].length != i2[0].length)
			return false;
		for(int j =0; j < i1.length; j++)
			for(int k = 0; k < i1[0].length; k++)
				if(i1[j][k] != i2[j][k])
					return false;
		return true;
	}

	boolean tried = false;
	
	@Override
	public int[][] getImage() {
		if(tried)
			return img;
		
		try {
			mClient.request(p1x, p1y, p2x, p2y, width, height); // async
			Thread.currentThread().sleep(2000);
		} catch (Exception e) {
			System.out.println("Error getting image: " + e.getMessage());
		}
		
		int[] response = mClient.response(); // waits on async tasks to return
		this.img = new int[width][height];
		for(int i = 0; i < response.length;i++){
			img[i%width][i/width] = response[i];
		}
		if(imageEqual(img, mFractal.getImage()))
			System.out.println("Server worked correcly");
		else
			System.out.println("Server failed to return correctly");
		tried = true;
		return img;
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
