package Server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Common.Credentials;
import Common.FTClient;
import Common.FTServer;
import Server.dao.AccountDAO;
import Server.model.Account;

public class Controller extends UnicastRemoteObject implements FTServer {
	AccountDAO acctDao=new AccountDAO();
	//FTClient remoteClient;
	
	public Controller() throws RemoteException {
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
	public boolean register(Credentials credentials) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(FTClient remoteClient, Credentials credentials) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
