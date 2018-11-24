package Server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import Common.RemoteFTClient;
import Common.RemoteFTServer;
import Common.SerializableCredentials;
import Server.dao.AccountOperationDAO;
import Server.model.Account;

public class RemoteController extends UnicastRemoteObject implements RemoteFTServer {
	private AccountOperationDAO acctDao=new AccountOperationDAO();
	private HashMap<Long, RemoteFTClient> clientMap=new HashMap<Long,RemoteFTClient>();
	//FTClient remoteClient;
	
	public RemoteController() throws RemoteException {
		//this.remoteClient=remoteClient;
	}

	@Override
	public boolean checkLoginState(long userId) throws RemoteException{
		// TODO Auto-generated method stub
		if(userId==0) {
			return false;
		}else {
			
		}
		return false;
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
	public long register(SerializableCredentials credentials) throws RemoteException {
		// TODO Auto-generated method stub
		return acctDao.createNewAccount(credentials);
	}

	@Override
	public long login (RemoteFTClient remoteClient, SerializableCredentials credentials) throws RemoteException {
		// TODO Auto-generated method stub
			
			Account account= acctDao.FindAccountByName(credentials.getUsername(), true);
			if(account.getUsername().equals(credentials.getUsername())&&account.getPassword().equals(credentials.getPassword())) {
				long loggedInUserId=account.getUserId();
				this.clientMap.put(loggedInUserId, remoteClient);
				return account.getUserId();
			}
			else return 0;
	}
	
	@Override
	public boolean userLeave(long userId) throws RemoteException{
		if(clientMap.get(userId)==null) {
			return false;
		}else {
			clientMap.remove(userId);
			return true;
		}
		
	}
}
			
			