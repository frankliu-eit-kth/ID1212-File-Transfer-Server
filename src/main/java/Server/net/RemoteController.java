package Server.net;

import java.io.File;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import Common.Credentials;
import Common.LocalFileController;
import Common.RemoteClient;
import Common.RemoteServer;
import Server.dao.AccountDAO;
import Server.dao.FileDao;
import Server.model.Account;
import Server.model.FileMeta;
import Server.model.FileWarehouse;

public class RemoteController extends UnicastRemoteObject implements RemoteServer {
	private AccountDAO acctDao=new AccountDAO();
	private FileDao fileDao=new FileDao();
	private HashMap<Long, RemoteClient> onlineClients=new HashMap<Long,RemoteClient>();
	//FTClient remoteClient;
	public static final String SERVER_FILE_DIRECTORY="C:\\Users\\m1339\\Desktop\\SERVER\\";
	public RemoteController() throws RemoteException {
		super();
	}
	@Override
	public void sendFile(String filename) throws RemoteException {
		FileMeta fileMeta=fileDao.findFile(filename);
		String url=fileMeta.getUrl();
		File file=LocalFileController.readFile(url);
		FileWarehouse.sendingStorage.put(filename, file);
		
	}
	@Override
	public String checkFilePermission(long userId, String filename) throws RemoteException {
		FileMeta file=fileDao.findFile(filename);
		Account acct=file.getOwner();
		if(userId==acct.getUserId()) {
			return "write";
		}
		else {
			return file.getPermission();
		}
	}
	
	@Override
	public boolean removeFile(String filename) throws RemoteException {
		if(!(LocalFileController.deleteFile(SERVER_FILE_DIRECTORY+filename))) {
			return false;
		}
		if(!fileDao.removeFile(filename)) {
			return false;
		}
		
		return true;
		
	}
	@Override
	public boolean checkFileOwner(long userId, String filename) throws RemoteException {
		Account owner=fileDao.findFileOwner(filename).getOwner();
		if(userId==owner.getUserId()) {
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public boolean changePermission(String filename, String permission) throws RemoteException {
		FileMeta file=fileDao.findFile(filename);
		file.setPermission(permission);
		fileDao.updateFileMeta(file);
		return true;
		
	}
	
	
	@Override
	public boolean checkFileExists(String filename) {
		return fileDao.checkFileExists(filename);
	}
	@Override
	public boolean updateFile(long userId, String filename) throws RemoteException {
		// TODO Auto-generated method stub
		RemoteClient clientConsole= onlineClients.get((Long)userId);
		Account client=acctDao.FindAccountById(userId,true);
		while(FileWarehouse.getFile(filename)==null) {
		}
		File file= FileWarehouse.getFile(filename);
		FileMeta filemeta=fileDao.findFile(filename);
		filemeta.setFilename(filename);
		filemeta.setOwner(client);
		filemeta.setPermission("read");
		filemeta.setSize((int)file.length());
		filemeta.setUrl(SERVER_FILE_DIRECTORY+filename);
		try {
			fileDao.updateFileMeta(filemeta);
		}catch(Exception e) {
			
			try {
				clientConsole.notify("database failure");
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			e.printStackTrace();
			return false;
		}
		try {
			LocalFileController.storeFile(SERVER_FILE_DIRECTORY, file);
		}catch(Exception e) {
			try {
				clientConsole.notify("file storage failure");
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	@Override
	public void storeFile(long userId,String filename) {
		RemoteClient clientConsole= onlineClients.get((Long)userId);
		Account client=acctDao.FindAccountById(userId,true);
		while(FileWarehouse.getFile(filename)==null) {
		}
		File file= FileWarehouse.getFile(filename);
		FileMeta filemeta=new FileMeta();
		filemeta.setFilename(filename);
		filemeta.setOwner(client);
		filemeta.setPermission("read");
		filemeta.setSize((int)file.length());
		filemeta.setUrl(SERVER_FILE_DIRECTORY+filename);
		try {
			fileDao.storeNewFileMeta(filemeta);
		}catch(Exception e) {
			
			try {
				clientConsole.notify("database failure");
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		try {
			LocalFileController.storeFile(SERVER_FILE_DIRECTORY, file);
		}catch(Exception e) {
			try {
				clientConsole.notify("file storage failure");
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	/*
	@Override
	public boolean checkLoginState(long userId) throws RemoteException{
		// TODO Auto-generated method stub
		if(userId==0) {
			return false;
		}else {
			
		}
		return false;
	}*/
	
	@Override
	public void listAll(RemoteClient remoteClient) throws RemoteException {
		// TODO Auto-generated method stub
		List<FileMeta> files=fileDao.findAll();
		for(FileMeta f: files) {
			String s=f.toString();
			remoteClient.notify(s);
		}
	}
	@Override
	public boolean checkUserExists(String username) throws RemoteException {
		// TODO Auto-generated method stub
		Account acct=acctDao.FindAccountByName(username, true);
		if(acct==null) {
			return false;
		}
		else {
			return true;
		}
		
	}

	@Override
	public long register(Credentials credentials) throws RemoteException {
		// TODO Auto-generated method stub
		return acctDao.persistNewAccount(credentials);
	}

	@Override
	public long login (RemoteClient remoteClient, Credentials credentials) throws RemoteException {
		// TODO Auto-generated method stub
			
			Account account= acctDao.FindAccountByName(credentials.getUsername(), true);
			if(account.getUsername().equals(credentials.getUsername())&&account.getPassword().equals(credentials.getPassword())) {
				long loggedInUserId=account.getUserId();
				this.onlineClients.put(loggedInUserId, remoteClient);
				return account.getUserId();
			}
			else return 0;
	}
	
	@Override
	public boolean clientLeave(long userId) throws RemoteException{
		if(onlineClients.get(userId)==null) {
			return false;
		}else {
			onlineClients.remove(userId);
			return true;
		}
		
	}
}
			
			