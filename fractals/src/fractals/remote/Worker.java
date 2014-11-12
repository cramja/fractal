package fractals.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by csehl on 11/11/14.
 */
public interface Worker extends Remote {
    public int run(int x, int y) throws RemoteException;
}
