package testSockets;

import java.net.*; // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*; // for IOException
import java.util.*;

public class UDPEchoServer implements Runnable {

	private static final int ECHOMAX = 255; // Maximum size of echo datagram
	private FileWriter mlogWriter = null;
	private Date mDate = new Date();
	
	private DatagramSocket mSocket;
	private DatagramPacket mPacket;
	private int mPort = 0;

	public static void main(String[] args) throws IOException {
		if (args.length != 1) // Test for correct argument list
			throw new IllegalArgumentException("Parameter(s): <Port>");

		int servPort = Integer.parseInt(args[0]);

		UDPEchoServer server = new UDPEchoServer(servPort);
		server.run();
	}

	public UDPEchoServer(int port) {
		mPacket = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
		mPort = port;
		log("Server Starting...");
	}

	public void run() {
		try {
			mSocket = new DatagramSocket(mPort);
		} catch (SocketException e) {
			log("Failed to create Server");
			System.exit(-1);
		}
		for (;;) { // Run forever, receiving and echoing datagrams
			try {
				mSocket.receive(mPacket);
			} catch (IOException e) {
				log("Exception in recieve: " + e.getMessage());
			}
			log("Handling request: " + mPacket.getAddress().getHostAddress() + " on port " + mPacket.getPort());
			try {
				mSocket.send(mPacket);
			} catch (IOException e) {
				log("Exception in send: " + e.getMessage());
			}
			mPacket.setLength(ECHOMAX); // Reset length to avoid shrinking buffer
		}
	}

	private void log(String message) {
		if(mlogWriter == null){
			try {
				mlogWriter =  new FileWriter(new File("log" + System.currentTimeMillis() + ".txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(mlogWriter != null){
			try {
				mDate.setTime(System.currentTimeMillis());
				mlogWriter.write(mDate.toString() + " "+ message + "\n");
				mlogWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}