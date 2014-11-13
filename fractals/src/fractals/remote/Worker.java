package fractals.remote;

import fractals.parallel.FractalCalculator;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Worker implements Slave {
    private static final int DEFAULT_PORT = 1099;
    private FractalCalculator fc;

    public void init(double r1, double i1, double r2, double i2,
                     int width, int height) throws RemoteException {
        fc = new FractalCalculator(r1, i1, r2, i2, width, height);
    }

    public int[][] run(double r1, double i1, double r2, double i2,
                   int width, int height) {
        System.out.println("Worker: Servicing request.");
        System.out.println("Worker: running at [" + r1 + ", " + i1 + "],[" + r2 + ", " + i2 + "] over " + width + " x " + height);
        fc.update(r1, i1, r2, i2, width, height);
        return fc.getImage();
    }

    public static void main(String[] args) {
        try {
            Worker obj = new Worker();
            Slave worker = (Slave) UnicastRemoteObject.exportObject(obj, 0);

            // Create a new registry
            // The registry is a way of knowing what remote objects are available for use
            Registry registry = LocateRegistry.createRegistry(DEFAULT_PORT);
            // Add our worker object to the registry
            registry.bind("worker", worker);

            System.out.println("Worker ready on port " + DEFAULT_PORT);
        } catch (Exception e) {
            System.err.println("Worker exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
