package fractals.udp;

import java.io.IOException; // for IOException
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress; // for DatagramSocket, DatagramPacket, and InetAddress
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fractals.parallel.Point;
import fractals.parallel.RenderTask;

public class Client{
	private static final int TIMEOUT = 500; 	// Resend timeout (milliseconds)
	
	private ExecutorService e = Executors.newFixedThreadPool(20);
	private ArrayList<ClientWorker> mWorkers = new ArrayList<ClientWorker>();
	private int mPort;
	
	private int[] mData;
	private int mWorkerWidth = 0;

	public Client(int port) {
		this.mPort = port;
	}

	public void addServer(String ip, int port) throws UnknownHostException{
		mWorkers.add(new ClientWorker(InetAddress.getByName(ip),port,TIMEOUT));
	}
	
	public void request(double p1x, double p1y, double p2x, double p2y, int height, int width) throws IOException, InterruptedException, ExecutionException {
		updateWorkers(p1x, p1y, p2x, p2y, width, height);
		Future[] futures = new Future[mWorkers.size()];
		for(int c = 0; c < mWorkers.size(); c++)
			futures[c] = e.submit(mWorkers.get(c));
		
		for(int c = 0; c < futures.length; c++) // ensure they all complete
			futures[c].get();
		
		mData = new int[width * height];
		for(int c = 0; c < mWorkers.size(); c++){ // copy back data
			ClientWorker cw = mWorkers.get(c);
			int sIndex  = cw.id *mWorkerWidth;
			int[] wData = cw.mData;
			int index = cw.id *mWorkerWidth;
			for(int i = 0; i < wData.length; i++){
				if((index - mWorkerWidth) % sIndex == 0 || index % width == 0){
					index += width;
				}
				mData[index++] = wData[i];
			}
		}
				
	}
	
	private void updateWorkers(double p1x, double p1y, double p2x, double p2y, int height, int width){
		if(mWorkers.size() == 1) {
			mWorkers.get(0).update(p1x, p1y, p2x, p2y, width, height, 0);
			mWorkerWidth = width;
		} else {
			double dx = Math.abs(p1x - p2x) / ((double) mWorkers.size());
			int twidth = (int) width/ mWorkers.size();
			int theight = height;
			mWorkerWidth = twidth;
			for (int t = 0; t < mWorkers.size() - 1; t++)
				mWorkers.get(t).update(p1x + dx * ((double) t), p1y, p1x + dx * ((double) (t + 1)), p2y, twidth,theight, t);
			mWorkers.get(mWorkers.size() - 1).update(p1x + dx*((double)mWorkers.size()-1), 
					p1y,p2x,p2y,width-twidth*(mWorkers.size()-1), theight, mWorkers.size()-1);
		}
	}
	
	public int[] response(){
		return mData;
	}

}