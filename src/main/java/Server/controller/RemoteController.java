package Server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Common.SerializableCredentials;
import Common.RemoteFTClient;
import Common.RemoteFTServer;
import Server.dao.AccountOperationDAO;
import Server.model.Account;

public class RemoteController extends UnicastRemoteObject implements RemoteFTServer {
	AccountOperationDAO acctDao=new AccountOperationDAO();
	//FTClient remoteClient;
	
	public RemoteController() throws RemoteException {
		//this.remoteClient=remoteClient;
	}

	@Override
	public boolean checkLoginState(long userId) throws RemoteException{
		// TODO Auto-generated method stub
		
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
	public boolean register(SerializableCredentials credentials) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(RemoteFTClient remoteClient, SerializableCredentials credentials) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
