package Common;

import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * provides server interfaces the client could remotely invoke
 * @author Liming Liu
 * @debug: illegal remote method in Java: add throws RemoteException to all mehtods
 */
public interface RemoteServer extends Remote {
	public static final String SERVER_NAME_IN_REGISTRY = "FILE_TRANSFER_SERVER";
	
	public boolean clientLeave(long userId) throws RemoteException;
	
	public void listAll(RemoteClient remoteClient) throws RemoteException;
	
	public boolean checkUserExists(String username) throws RemoteException;

	public long register(Credentials credentials) throws RemoteException;
	
	public long login(RemoteClient remoteClient,Credentials credentials) throws RemoteException;

	public boolean checkFileExists(String filename) throws RemoteException;
	
	public void storeFile(long userId, String filename) throws RemoteException;
	
	public boolean checkFileOwner(long userId,String filename) throws RemoteException;
	
	public boolean changePermission(String filename,String permission) throws RemoteException;
	
	public String checkFilePermission(long userId,String filename) throws RemoteException;
	
	public boolean removeFile(String filenanme) throws RemoteException;
	
	public boolean updateFile(long userId, String filename) throws RemoteException;
	
	public void sendFile(String filename) throws RemoteException;

}
