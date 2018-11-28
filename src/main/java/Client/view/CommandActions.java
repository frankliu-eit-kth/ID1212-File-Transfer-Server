package Client.view;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Client.controller.NetworkController;
import Client.net.OutputHandler;
import Common.Credentials;
import Common.LocalFileController;
import Common.RemoteClient;
import Common.RemoteServer;
/**
 * actions separated from nonblocking interpreter
 * @author Liming Liu
 *
 */
public class CommandActions {
	/**
     * local file folder
     */
    private String DEFAULT_LOCAL_FOLDER="C:\\Users\\m1339\\Desktop\\CLIENT\\";
    /**
     * remote-method-invoking objects
     * myRemoteObj: created locally ( a RemoteOutputConsole) and pass to server through remote methods, used to handle the message( just like a remote output controller)
     * remoteServer: the remote controller fetch from registry and invoke its methods locally do remote operations
     */
    private final RemoteClient myRemoteObj;
    private RemoteServer remoteServer;
    /**
     * the local output handler pass to the network layer to deliver message and file
     */
    private final OutputHandler localOutputHandler;
    /**
     * user status
     */
    private long myIdAtServer;
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    /**
     * network utilities and params
     */
    private NetworkController netController;
    private final int SERVER_PORT=8080;
    /**
     * constructor
     * @throws RemoteException
     */
	public CommandActions() throws RemoteException {
		 myRemoteObj = new RemoteConsoleOutput();
	     localOutputHandler=new localConsoleOutput();
	     netController=new NetworkController();
	}
	 /**
     * utility
     * @param filename
     * @return
     * @throws Exception
     */
    public boolean checkHavePermission(String filename) throws Exception{
    	if(remoteServer.checkFilePermission(myIdAtServer,filename).equals("read")) {
    		outMgr.println("you do not have permission to remove file");
    		return false;
    	}
    	return true;
    }
    /**
     * utility
     * @return
     */
    public boolean checkUserLoggedIn() {
    	if(myIdAtServer==0) {
    		outMgr.println("please log in first");
    		return false;
    	}
    	return true;
    }
    /**
	 * look up for server in registry, get the remote controller stub
	 */
    public void lookupServer(String host) throws NotBoundException, MalformedURLException,
                                                  RemoteException {
        remoteServer = (RemoteServer) Naming.lookup(
                "//" + host + "/" + RemoteServer.SERVER_NAME_IN_REGISTRY);
    }
    /**
     * remove file from server
     * @param cmdLine
     * @throws Exception
     */
    public void remove(CmdLineParser cmdLine) throws Exception {
    	if(!checkUserLoggedIn())return; 
    	String filename= cmdLine.getParameter(0);
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file not exist");
    		return;
    	}
    	if(!checkHavePermission(filename)) {
    		return;
    	}else {
    		if(remoteServer.removeFile(filename)) {
    			outMgr.println("remove done");
    			return;
    		}else {
    			outMgr.println("remove failed");
    			return;
    		}
    	}
    }
    /**
     * update file meta in database
     * send new file to server and update in file directory
     * @param cmdLine
     * @throws Exception
     */
    public void update(CmdLineParser cmdLine) throws Exception{
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
    	File localFile=null;
    	
		try {
			localFile = LocalFileController.readFile(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			outMgr.println("wrong directory, please try again");
			return;
		}
        if(localFile==null) {
        	outMgr.println("wrong file path, please try again");
        	return;
        }
    	netController.sendFile(localFile, localOutputHandler);
    	remoteServer.updateFile(this.myIdAtServer,filename);
    	return;
    }
    /**
     * store file meta in db
     * store file in server directory
     * @param cmdLine
     * @throws Exception
     */
    public void store(CmdLineParser cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	String filename= cmdLine.getParameter(0);
    	outMgr.println("sending "+filename);
    	String url=cmdLine.getParameter(1);
    	File localFile=null;
		try {
			localFile = LocalFileController.readFile(url);
		} catch (Exception e) {
			outMgr.println("wrong directory, please try again");
			return;
		}
        if(localFile==null) {
        	outMgr.println("wrong file path, please try again");
        	return;
        }
    	if(remoteServer.checkFileExists(filename)) {
    		outMgr.println("file exists, please choose update command");
    		return;
    	}
    	netController.sendFile(localFile, localOutputHandler);
    	remoteServer.storeFile(this.myIdAtServer,filename);
    	return;
    }
    /**
     * update file permission in db
     * @param cmdLine
     * @throws Exception
     */
    public void permission(CmdLineParser cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return; 
    	String filename= cmdLine.getParameter(0);
    	String permission=cmdLine.getParameter(1);
    	if(!(permission.equals("read")||permission.equals("write"))){
    		outMgr.println("illegal permission");
    		return;
    	}
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file not exist");
    		return;
    	}
    	if(!remoteServer.checkFileOwner(myIdAtServer, filename)) {
    		outMgr.println("you have no permission to update this file");
    		return;
    	}else {
    		if(remoteServer.changePermission(filename,permission)) {
    			outMgr.println("update succeeded");
    		}else {
    			outMgr.println("update failed");
    		}
    	}
    	return;
    }
    /**
     * retrieve file from server and store in the file folder read from command line
     * @param cmdLine
     * @throws Exception
     */
    public void retrieve(CmdLineParser cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	String filename= cmdLine.getParameter(0);
    	if(!remoteServer.checkFileExists(filename)) {
    		outMgr.println("file not exist");
    		return;
    	}
    	String locationFolder=cmdLine.getParameter(1);
    	this.localOutputHandler.setFileFolder(locationFolder);
    	remoteServer.sendFile(filename);
    	netController.sendFileRequest(filename);
    	return;
    }
    /**
     * connect to the file transfer server
     * fetch stub from registry
     * 
     * @param cmdLine
     * @throws Exception
     */
    public void connect(CmdLineParser cmdLine) throws Exception{
    	String host=cmdLine.getParameter(0);
    	lookupServer(host);
    	netController.connect(host, SERVER_PORT, localOutputHandler);
    	//outMgr.println("successfully fetch remote controller "+ remoteServer.SERVER_NAME_IN_REGISTRY);
    	return;
    }
    /**
     * register new user account
     * @param cmdLine
     * @throws Exception
     */
    public void register(CmdLineParser cmdLine) throws Exception{
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
    /**
     * login with username and password
     * @param cmdLine
     * @throws Exception
     */
    public void login(CmdLineParser cmdLine) throws Exception{
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
    	outMgr.println("welcome "+username+" ! Your user Id is "+ this.myIdAtServer);
    	return;
    }
    /**
     * list all file metas
     * @param cmdLine
     * @throws Exception
     */
    public void listall(CmdLineParser cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	remoteServer.listAll(myRemoteObj);
    	return;
    }
    /**
     * quit the account and disconnect from server
     * @param cmdLine
     * @throws Exception
     */
    public void quit(CmdLineParser cmdLine) throws Exception{
    	if(myIdAtServer==0) {
    		netController.disconnect();
    		outMgr.println("quit done");
    		return; 
    	}else {
    		if(remoteServer.clientLeave(myIdAtServer)) {
    			
    			this.remoteServer=null;
    			this.myIdAtServer=0;
    			boolean forceUnexport = false;
                UnicastRemoteObject.unexportObject(myRemoteObj, forceUnexport);
                netController.disconnect();
    			outMgr.println("quit done");
    		}
    		else {
    			outMgr.println("quit failed");
    		}
    	}
    	return;
    	
    }
	/**
	 * a remote client instance： used to notify messages to client from server side
	 */
    private class RemoteConsoleOutput extends UnicastRemoteObject implements RemoteClient{
        public RemoteConsoleOutput() throws RemoteException {
        }
        @Override
        public void notify(String msg) {
        	outMgr.println(msg);
        }
    }
    /**
     * a local output handler instance
     * pass msg and file from network layer to view layer
     */
    private class localConsoleOutput implements OutputHandler{
    	private String localDirectory=DEFAULT_LOCAL_FOLDER;
    	
    	@Override
    	public void setFileFolder(String fileFolder) {
    		this.localDirectory=fileFolder;
    	}
		@Override
        public void handleMsg(String msg) {
              outMgr.println((String) msg);
        }
        @Override
        public void handleFile(File file) {
        	String filename=file.getName();
          	outMgr.println(filename+" received");
          	try {
				LocalFileController.makeDir(localDirectory);
			} catch (Exception e) {
				System.out.println("illegal directory, please try again");
				return;
			}
          	LocalFileController.storeFile(localDirectory, file);
        }
    }
}
