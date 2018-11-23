package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FTServer extends Remote {
	 public static final String SERVER_NAME_IN_REGISTRY = "File_Transfer_SERVER";
	
	public boolean checkLoginState(long userId) throws RemoteException;
	
	public boolean checkUserExists(String username) throws RemoteException;
	
	public boolean register(Credentials credentials) throws RemoteException;
	
	public void login(FTClient remoteClient,Credentials credentials) throws RemoteException;
}
