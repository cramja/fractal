package fractals.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Slave extends Remote {
    public void init(double r1, double i1, double r2, double i2,
                     int width, int height) throws RemoteException;
    public int[][] run(double r1, double i1, double r2, double i2,
                       int width, int height) throws RemoteException;
}
