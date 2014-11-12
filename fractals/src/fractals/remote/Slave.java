package fractals.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Slave extends Remote {
    public int run(int[][] array) throws RemoteException;
}
