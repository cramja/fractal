package fractals.remote;

import fractals.parallel.Point;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Master {
    private static String[] hosts = new String[1];
    List<Slave> workers = new ArrayList<>();
    Point[] bounds;
    int totalWidth;
    int totalHeight;
    int widthStride;

    public Master() {
        try {
            // BOILERPLATE
            // Read in hosts from workers.txt
            try {
                hosts = Files.readAllLines(new File("./workers.txt").toPath(), Charset.defaultCharset()).toArray(hosts);
            } catch (IOException e) {
                System.err.println("Unable to read workers file, using 127.0.0.1");
                hosts = new String[]{ "127.0.0.1" };
            }
            for (int i = 0; i < hosts.length; i++) {
                // Ask a remote machine for their registry
                Registry registry = LocateRegistry.getRegistry(hosts[i]);

                // Ask for information on each registry to test the connection
                try {
                    // Get a pointer to a remote object
                    workers.add((Slave) registry.lookup("worker"));
                } catch (RemoteException e) {
                    System.out.println("Remote host " + hosts[i] + " unable to be contacted.");
                }
            }
        } catch(RemoteException | NotBoundException e) {
            System.err.println(e);
        }
    }


    // NON-BOILERPLATE
    public int numWorkers() {
        return workers.size();
    }

    // Do something to initialize workers
    public void initWorkers(int numWorkersToEngage, int width, int height) {
        try {
            workers = workers.subList(0, numWorkersToEngage);
            totalWidth = width;
            totalHeight = height;
            widthStride = width/workers.size();

            for(int i=0; i<workers.size(); i++) {
                workers.get(i).init(rStride()*i + bounds[0].x, bounds[0].y,
                        rStride()*i + rStride() + bounds[0].x, bounds[1].y,
                        widthStride, totalHeight);
            }
        } catch(IndexOutOfBoundsException e) {
            System.err.println("More workers requested than available");
            e.printStackTrace();
        } catch(RemoteException e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void updateBounds(Point[] bounds) {
        this.bounds = bounds;
    }

    public double rStride() {
        return Math.abs(bounds[1].x - bounds[0].x)/workers.size();
    }

    public int[][] mergeImage(int[][][] parts) {
        int[][] mergedImage = new int[totalWidth][totalHeight];
        for(int i=0;i<workers.size();i++) {
            System.arraycopy(parts[i], 0, mergedImage, widthStride*i, widthStride);
        }
        return mergedImage;
    }

    // available objects:
    //   slaves - an array of machines able to execute the `run()` method
    public int[][] getImage(int width, int height) {
        try {
            totalWidth = width;
            totalHeight = height;
            widthStride = width/workers.size();
            int[][][] parts = new int[workers.size()][widthStride][totalHeight];

            // Give each worker a section of the fractal to compute
            // by dividing the image up into vertical strips
            for(int i=0; i<workers.size(); i++) {
                parts[i] = workers.get(i).run(rStride()*i + bounds[0].x, bounds[0].y,
                                              rStride()*i + rStride() + bounds[0].x, bounds[1].y,
                                              widthStride, totalHeight);
            }
            return mergeImage(parts);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }
}
