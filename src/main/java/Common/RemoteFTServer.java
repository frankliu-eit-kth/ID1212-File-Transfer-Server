package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteFTServer extends Remote {
	 public static final String SERVER_NAME_IN_REGISTRY = "FILE_TRANSFER_SERVER";
	
	public boolean checkLoginState(long userId) throws RemoteException;
	
	public boolean userLeave(long userId) throws RemoteException;
	
	public boolean checkUserExists(String username) throws RemoteException;
	/**
	 * return new userId
	 * 
	 * @param credentials
	 * @return
	 * @throws RemoteException
	 */
	public long register(SerializableCredentials credentials) throws RemoteException;
	/**
	 * return user id
	 * @param remoteClient
	 * @param credentials
	 * @return
	 * @throws RemoteException
	 */
	public long login(RemoteFTClient remoteClient,SerializableCredentials credentials) throws RemoteException;
}
