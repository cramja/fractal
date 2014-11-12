package fractals.remote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Master {

    private Master() {}

    private static final String[] hosts = {
            "127.0.0.1",
            "babbage.lab.knet.edu"
    };

    public static void main(String[] args) {
        try {
            // BOILERPLATE

            ArrayList<Slave> slaves = new ArrayList<Slave>();
            for(int i=0; i<hosts.length; i++) {
                // Ask a remote machine for their registry
                Registry registry = LocateRegistry.getRegistry(hosts[i]);

                // Ask for information on each registry to test the connection
                try {
                    // Get a pointer to a remote object
                    slaves.add((Slave) registry.lookup("worker"));
                } catch (RemoteException e) {
                    System.out.println("Remote host " + hosts[i] + " unable to be contacted.");
                }
            }


            // NON-BOILERPLATE

            // available objects:
            //   slaves - an array of machines able to execute the `run()` method

            int[][] array = new int[1000][1000];
            for(int i=0;i<array.length;i++) {
                for(int j=0;j<array.length;j++) {
                    array[i][j] = 1;
                }
            }

            for(Slave slave : slaves) {
                int response = slave.run(array);
                System.out.println("Slave Response: " + response);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
