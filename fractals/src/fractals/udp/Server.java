package fractals.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket; // for DatagramSocket, DatagramPacket, and InetAddress
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fractals.parallel.FractalCalculator;

public class Server implements Runnable {

	private static final int MAXINTS = 40;
	private static final int BUFFSIZE = 756; // Maximum size of echo datagram
	private DatagramSocket mSocket;
	private DatagramPacket mPacket;
	private int mPort = 0;

	private int id = -1;

	private FractalCalculator mCalculator;

	public Server(int port) {
		mPacket = new DatagramPacket(new byte[BUFFSIZE], BUFFSIZE);
		mPort = port;

		mCalculator = new FractalCalculator(0, 0, 0, 0, 20, 20); // TODO
	}

	public void run() {
		try {
			mSocket = new DatagramSocket(mPort);
		} catch (SocketException e) {
			System.out.println("Failed to create Server");
			System.exit(-1);
		}
		while(true) { // Run forever, receiving and echoing datagrams
			try {
				mSocket.receive(mPacket);
			} catch (IOException e) {
				System.out.println("Exception in recieve: " + e.getMessage());
			}
			long s_time = System.currentTimeMillis();
			System.out.println("Handling request: "
					+ mPacket.getAddress().getHostAddress() + " on port "
					+ mPacket.getPort());

			unmarshal(mPacket.getData());
			int[][] responseArr = mCalculator.getImage(); // does calculations

			ArrayList<byte[]> payload = marshal(responseArr);
			int sent = 0;
			for (byte[] args : payload) {
				DatagramPacket packet = new DatagramPacket(args, args.length, mPacket.getAddress(), mPacket.getPort());
				sent++;
				try {
					mSocket.send(packet);
				} catch (IOException e) {
					System.out.println("Exception in send: " + e.getMessage());
				}
			}
			System.out.println("Took: " + (System.currentTimeMillis() - s_time)
					+ " ms to serve request");
			System.out.println("Total of " + sent + " packets sent.");
		}
	}

	private void unmarshal(byte[] args) {
		System.out.print( new String(mPacket.getData()));
		String[] arr = new String(mPacket.getData()).split(",");
		System.out.println("Incoming args: " + args[0] + "," + args[1] + ","
				+ args[2] + "," + args[3] + "," + args[4] + "," + args[5] + ","
				+ args[6]);

		this.id = Integer.parseInt(arr[0]);

		
		mCalculator.update(Double.parseDouble(arr[1]),
				Double.parseDouble(arr[2]), Double.parseDouble(arr[3]),
				Double.parseDouble(arr[4]), Integer.parseInt(arr[5]),
				Integer.parseInt(arr[6]));
	}

	// packet structure: [id,index,data...]
	private ArrayList<byte[]> marshal(int[][] data) {
		ArrayList<byte[]> ret = new ArrayList<byte[]>();

		// flatten data:
		int[] flatten = new int[data.length * data[0].length];
		for (int i = 0; i < flatten.length; i++) {
			flatten[i] = data[i % data.length][i / data.length];
		}
		int pIndex = 0;
		while (pIndex < flatten.length) {
			StringBuilder sb = new StringBuilder();
			sb.append(id);
			sb.append(",");
			sb.append(pIndex);
			for (int i = 0; i < MAXINTS; i++) {
				if (pIndex == flatten.length)
					break;
				sb.append(",");
				sb.append(flatten[pIndex++]);
			}
			sb.append(",");
			String args = sb.toString();
			ret.add(args.getBytes());
		}
		return ret;
	}
	
	public static void main(String[] args) {
		int port = 2000;
		if (args.length > 0)
			port = Integer.parseInt(args[0]);

		try {
			System.out.println("Starting server address: "
					+ InetAddress.getLocalHost() + " on port: " + port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	Thread theThread = new Thread(new Server(2001));
	//	theThread.start();

		Server server = new Server(port);
		server.run();
	}

}