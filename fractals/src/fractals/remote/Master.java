package fractals.remote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by csehl on 11/11/14.
 */
public class Master {

    private Master() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            RemoteWorker stub = (RemoteWorker) registry.lookup("worker");
            int response = stub.run(1,2);
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
