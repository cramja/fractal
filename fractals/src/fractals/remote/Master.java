package fractals.remote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Master {

    private Master() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            // BOILERPLATE
            // Ask a remote machine for their registry
            Registry registry = LocateRegistry.getRegistry(host);
            // Get a pointer to a remote object
            Slave stub = (Slave) registry.lookup("worker");


            // NON-BOILERPLATE
            // Run a method
            int response = stub.run(2,2);
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
