package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
	public void notify(String msg) throws RemoteException;
}
