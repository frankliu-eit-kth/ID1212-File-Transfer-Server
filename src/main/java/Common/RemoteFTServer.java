package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteFTServer extends Remote {
	 public static final String SERVER_NAME_IN_REGISTRY = "FILE_TRANSFER_SERVER";
	
	public boolean checkLoginState(long userId) throws RemoteException;
	
	public boolean checkUserExists(String username) throws RemoteException;
	
	public boolean register(SerializableCredentials credentials) throws RemoteException;
	
	public void login(RemoteFTClient remoteClient,SerializableCredentials credentials) throws RemoteException;
}
