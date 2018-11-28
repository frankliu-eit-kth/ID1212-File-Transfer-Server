package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * provides interfaces that server side could invoke remotely
 * @author Frank
 *
 */
public interface RemoteClient extends Remote {
	/**
	 * notify the client with certain messages
	 * @param msg
	 * @throws RemoteException
	 */
	public void notify(String msg) throws RemoteException;
}
