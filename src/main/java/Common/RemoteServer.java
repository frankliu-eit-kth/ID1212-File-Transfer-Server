package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
	 public static final String SERVER_NAME_IN_REGISTRY = "FILE_TRANSFER_SERVER";
	
	//public boolean checkLoginState(long userId) throws RemoteException;
	
	public boolean clientLeave(long userId) throws RemoteException;
	
	public boolean checkUserExists(String username) throws RemoteException;
	/**
	 * return new userId
	 * 
	 * @param credentials
	 * @return
	 * @throws RemoteException
	 */
	public long register(Credentials credentials) throws RemoteException;
	/**
	 * return user id
	 * @param remoteClient
	 * @param credentials
	 * @return
	 * @throws RemoteException
	 */
	public long login(RemoteClient remoteClient,Credentials credentials) throws RemoteException;
}
