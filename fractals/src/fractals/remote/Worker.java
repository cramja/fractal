package fractals.remote;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by csehl on 11/11/14.
 */
public class Worker implements Slave {
    public int run(int x, int y) {
        System.out.println("Worker: Servicing request.");
        return x+y;
    }

    private static final int DEFAULT_PORT = 1099;

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
