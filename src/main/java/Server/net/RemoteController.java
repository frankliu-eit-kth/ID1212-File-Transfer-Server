package Server.net;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import Common.Credentials;
import Common.RemoteClient;
import Common.RemoteServer;
import Server.dao.AccountDAO;
import Server.dao.FileDao;
import Server.model.Account;
import Server.model.FileMeta;

public class RemoteController extends UnicastRemoteObject implements RemoteServer {
	private AccountDAO acctDao=new AccountDAO();
	private FileDao fileDao=new FileDao();
	private HashMap<Long, RemoteClient> onlineClients=new HashMap<Long,RemoteClient>();
	//FTClient remoteClient;
	
	public RemoteController() throws RemoteException {
		super();
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
			
			