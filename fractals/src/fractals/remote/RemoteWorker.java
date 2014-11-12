package fractals.remote;

import sun.tools.jconsole.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by csehl on 11/11/14.
 */
public class RemoteWorker implements Worker {
    public int run(int x, int y) {
        return x+y;
    }

    public static void main(String[] args) {
        try {
            RemoteWorker obj = new RemoteWorker();
            Worker worker = (Worker) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("worker", worker);

            System.err.println("Worker ready");
        } catch (Exception e) {
            System.err.println("Worker exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
