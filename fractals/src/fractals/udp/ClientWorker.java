package fractals.udp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

public class ClientWorker implements Runnable {
	private static final int MAXTRIES = 5; // Maximum retransmissions
	private static final int BUFFSIZE = 1000; // bytes per packet

	InetAddress serverAddr;
	int serverPort;
	int timeout;

	double p1x;
	double p1y;
	double p2x;
	double p2y;
	int width;
	int height;

	int id;

	int[] mData;

	public ClientWorker(InetAddress ip, int port, int timeout) {
		this.timeout = timeout;
		this.serverAddr = ip;
		this.serverPort = port;
	}

	@Override
	public void run() {
		mData = new int[width * height];
		byte[] bytesToSend = marshal();

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(timeout / (MAXTRIES + 1));
		} catch (SocketException exc) {
			System.out.println("Failed making socket in worker: " + id);
			exc.printStackTrace();
		}

		DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
				bytesToSend.length, serverAddr, serverPort);

		DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFSIZE],
				BUFFSIZE);
		int tries = 0;
		boolean receivedResponse = false;
		try {
			socket.send(sendPacket);
		} catch (IOException exc) {
			System.out.println("Failed sending packet in worker: " + id);
			exc.printStackTrace();
		}
		long s_time = System.currentTimeMillis();
		while (System.currentTimeMillis() - s_time < 500) {
			try {
				socket.receive(receivePacket);
				System.out.println("Local Address: " + socket.getLocalSocketAddress());
				if (!receivePacket.getAddress().equals(serverAddr))
					System.out.println("Packet came from unknown source: " + receivePacket.getAddress());
				receivedResponse = true;
			} catch (InterruptedIOException e) { // We did not get anything
				tries += 1;
				System.out.println("Worker " + id + " timed out, " + (MAXTRIES - tries) + " more tries...");
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (receivedResponse) {
				if (unmarshal(receivePacket.getData()))
					System.out.println("Worker " + id+ " successfully unmarshalled response");
				else
					System.out.println("Worker " + id + " failed unmarshalling");
				receivePacket.setLength(BUFFSIZE);
			} else {
				System.out.println("Worker " + id
						+ " no response -- giving up.");
			}
		}

		socket.close();
	}

	public int[] getData() {
		return mData;
	}

	public void update(double p1x, double p1y, double p2x, double p2y,
			int twidth, int theight, int index) {
		this.id = index;
		this.p1x = p1x;
		this.p1y = p1y;
		this.p2x = p2x;
		this.p2y = p2y;
		width = twidth;
		height = theight;
	}

	private byte[] marshal() {
		String args = String.format("%d,%f,%f,%f,%f,%d,%d,", id, p1x, p1y, p2x,
				p2y, height, width);
		byte[] data = args.getBytes();
		System.out.println("Worker " + id + " sending request len: "
				+ data.length + " args: " + args);
		return data;
	}

	private boolean unmarshal(byte[] response) {
		String[] rvals = new String(response).split(",");
		int arg0 = Integer.parseInt(rvals[0]);
		if (id != arg0) {
			System.out.println("Worker " + id + " got wrong packet! " + arg0);
			return false;
		}
		int arg1 = Integer.parseInt(rvals[1]); // index
		int rindex = 2;
		while (rindex < rvals.length - 1){
			try{
				mData[arg1++] = Integer.parseInt(rvals[rindex++]);
			} catch (NumberFormatException e){
				System.out.println("num format");
			}
		}
		return true;
	}

}
