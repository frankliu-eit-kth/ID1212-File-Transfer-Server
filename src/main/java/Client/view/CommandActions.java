package Client.view;

import java.io.File;
import java.rmi.server.UnicastRemoteObject;

import Common.Credentials;
import Common.LocalFileController;

public class CommandActions {
	/**
     * local file folder
     */
    public String DEFAULT_LOCAL_FOLDER="C:\\Users\\m1339\\Desktop\\CLIENT\\";
    RemoteServer remoteServer;
    RemoteClient remoteClient;
    
	public CommandActions(RemoteServer remoteServer,RemoteClient myRemoteObj) {
		// TODO Auto-generated constructor stub
	}
	public void remove(CmdLine cmdLine) throws Exception {
    	if(!checkUserLoggedIn())return; 
    	String filename= cmdLine.getParameter(0);
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file does not exist");
    		return;
    	}
    	if(!checkHavePermission(filename)) {
    		return;
    	}else {
    		if(remoteServer.removeFile(filename)) {
    			outMgr.println("successfully remove");
    			return;
    		}else {
    			outMgr.println("remove failed");
    			return;
    		}
    	}
    }
    public void update(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return; 
    	
    	String filename= cmdLine.getParameter(0);
    	String url=cmdLine.getParameter(1);
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file does not exist");
    		return;
    	}
    	if(!checkHavePermission(filename)) {
    		return;
    	}
    	File localFile= LocalFileController.readFile(url);
        
    	if(localFile==null) {
    		outMgr.println("wrong directory, please try again");
    		return;
    	}
    	netController.sendFile(localFile, localOutputHandler);
    	remoteServer.updateFile(this.myIdAtServer,filename);
    	
    	return;
    }
    public void store(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	String filename= cmdLine.getParameter(0);
    	outMgr.println(filename);
    	String url=cmdLine.getParameter(1);
    	File localFile= LocalFileController.readFile(url);
    
    	if(localFile==null) {
    		outMgr.println("wrong directory, please try again");
    		return;
    	}
    	if(remoteServer.checkFileExists(filename)) {
    		outMgr.println("file already exists, please choose update command");
    		return;
    	}
    	netController.sendFile(localFile, localOutputHandler);
    	remoteServer.storeFile(this.myIdAtServer,filename);
    	return;
    }
    public void permission(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return; 
    	String filename= cmdLine.getParameter(0);
    	String permission=cmdLine.getParameter(1);
    	if(!(permission.equals("read")||permission.equals("write"))){
    		outMgr.println("illegal permission");
    		return;
    	}
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file does not exist");
    		return;
    	}
    	if(!remoteServer.checkFileOwner(myIdAtServer, filename)) {
    		outMgr.println("you have no right to update this file");
    		return;
    	}else {
    		if(remoteServer.changePermission(filename,permission)) {
    			outMgr.println("update successful！");
    		}else {
    			outMgr.println("update failed");
    		}
    	}
    	return;
    }
    public void retrieve(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	String filename= cmdLine.getParameter(0);
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file does not exist");
    		return;
    	}
    	String locationFolder=cmdLine.getParameter(1);
    	remoteServer.sendFile(filename);
    	netController.sendFileRequest(filename);
    	return;
    }
    public void connect(CmdLine cmdLine) throws Exception{
    	String host=cmdLine.getParameter(0);
    	lookupServer(host);
    	outMgr.println(host);
    	netController.connect(host, SERVER_PORT, localOutputHandler);
    	outMgr.println("successful connected to "+ remoteServer.SERVER_NAME_IN_REGISTRY);
    	return;
    }
    public void register(CmdLine cmdLine) throws Exception{
    	//lookupServer(cmdLine.getParameter(0));
    	String username=cmdLine.getParameter(0);
    	String password=cmdLine.getParameter(1);
    	if(myIdAtServer!=0) {
    		outMgr.println("you have already logged in, please log out");
    		return;
    	}
    	if(remoteServer.checkUserExists(username)) {
    		outMgr.println("user exists, please retry");
    		return;
    	}
    	Credentials credentials=new Credentials(username,password);
    	long newUserId=remoteServer.register(credentials);
    	outMgr.println("welcome "+username+" ! You've registered! Your user Id is "+ newUserId);
    	return;
    }
    public void login(CmdLine cmdLine) throws Exception{
    	//lookupServer(cmdLine.getParameter(0));
    	String username=cmdLine.getParameter(0);
    	String password=cmdLine.getParameter(1);
    	//outMgr.println("Test: username "+username);
    	
    	if(myIdAtServer!=0) {
    		outMgr.println("you have already logged in, please log out");
    		return;
    		
    	}
    	if(!remoteServer.checkUserExists(username)) {
    		outMgr.println("user does not exist, please retry");
    		return;
    	}
    	Credentials credentials=new Credentials(username,password);
    	this.myIdAtServer=remoteServer.login(myRemoteObj, credentials);
    	if(myIdAtServer==0) {
    		outMgr.println("user name does not match the password, please try again");
    		return;
    	}
    	outMgr.println("welcome "+username+" ! You've logged in! Your user Id is"+ this.myIdAtServer);
    	return;
    }
    public void listall(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	remoteServer.listAll(myRemoteObj);
    	return;
    }
    public void quit(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn()) {
    		return; 
    	}else {
    		if(remoteServer.clientLeave(myIdAtServer)) {
    			receivingCmds = false;
    			this.remoteServer=null;
    			this.myIdAtServer=0;
    			boolean forceUnexport = false;
                UnicastRemoteObject.unexportObject(myRemoteObj, forceUnexport);
    			outMgr.println("succssfully quit");
    		}
    		else {
    			outMgr.println("quit failed, please try again");
    		}
    	}
    	return;
    	
    }
}
