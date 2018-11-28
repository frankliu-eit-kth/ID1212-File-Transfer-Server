/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Client.view;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;

import Client.controller.NetworkController;
import Client.net.OutputHandler;
import Common.Credentials;
import Common.LocalFileController;
import Common.RemoteClient;
import Common.RemoteServer;

/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 * @author Frank
 */
public class NonBlockingInterpreter implements Runnable {
	/**
	 * console objects
	 */
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private boolean receivingCmds = false;
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
    /**
     * network utilities and params
     */
    private NetworkController netController;
    private final int SERVER_PORT=8080;
    /**
     * local file folder
     */
    private String DEFAULT_LOCAL_FOLDER="C:\\Users\\m1339\\Desktop\\CLIENT\\";
    
    public NonBlockingInterpreter() throws RemoteException {
        myRemoteObj = new RemoteConsoleOutput();
        localOutputHandler=new localConsoleOutput();
        netController=new NetworkController();
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     */
    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }
    /**
     * utility
     * @return
     */
    private boolean checkUserLoggedIn() {
    	if(myIdAtServer==0) {
    		outMgr.println("you have not logged in");
    		return false;
    	}
    	return true;
    }
    /**
     * utility
     * @param filename
     * @return
     * @throws Exception
     */
    private boolean checkHavePermission(String filename) throws Exception{
    	if(remoteServer.checkFilePermission(myIdAtServer,filename).equals("read")) {
    		outMgr.println("you do not have permission to remove file");
    		return false;
    	}
    	return true;
    }
    
    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                case REMOVE:
                	remove(cmdLine);
                	break;
                case UPDATE:
                	update(cmdLine);
                	break;
                case STORE:
                	store(cmdLine);
                	break;
                case PERMISSION:
                	permission(cmdLine);
                	break;
                case RETRIEVE:
                	retrieve(cmdLine);
                	break;
                case CONNECT:
                	connect(cmdLine);
                	break;
                case REGISTER:
                	register(cmdLine);
                	break;
                case LOGIN:
                	login(cmdLine);
                	break;
                case LISTALL:
                	listall(cmdLine);
                	break;
                case QUIT:
                	quit(cmdLine);
                	break;
                	
                default:
                	break;
                } 
            }catch (Exception e) {
                outMgr.println("Operation failed");
                e.printStackTrace();
            }
        }
    }

    private void remove(CmdLine cmdLine) throws Exception {
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
    private void update(CmdLine cmdLine) throws Exception{
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
    private void store(CmdLine cmdLine) throws Exception{
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
    private void permission(CmdLine cmdLine) throws Exception{
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
    private void retrieve(CmdLine cmdLine) throws Exception{
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
    private void connect(CmdLine cmdLine) throws Exception{
    	String host=cmdLine.getParameter(0);
    	lookupServer(host);
    	outMgr.println(host);
    	netController.connect(host, SERVER_PORT, localOutputHandler);
    	outMgr.println("successful connected to "+ remoteServer.SERVER_NAME_IN_REGISTRY);
    	return;
    }
    private void register(CmdLine cmdLine) throws Exception{
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
    private void login(CmdLine cmdLine) throws Exception{
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
    private void listall(CmdLine cmdLine) throws Exception{
    	if(!checkUserLoggedIn())return;
    	remoteServer.listAll(myRemoteObj);
    	return;
    }
    private void quit(CmdLine cmdLine) throws Exception{
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
    /**
	 * look up for server in registry, get the remote controller stub
	 */
    private void lookupServer(String host) throws NotBoundException, MalformedURLException,
                                                  RemoteException {
        remoteServer = (RemoteServer) Naming.lookup(
                "//" + host + "/" + RemoteServer.SERVER_NAME_IN_REGISTRY);
    }
    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }
	/**
	 * 
	 * a remote client instance： used to notify messages to client from server side
	 *
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
     *
     */
    private class localConsoleOutput implements OutputHandler{
		@Override
          public void handleMsg(String msg) {
              outMgr.println((String) msg);
          }
          @Override
          public void handleFile(File file) {
          	String filename=file.getName();
          	outMgr.println(filename+" received");
          	LocalFileController.makeDir(DEFAULT_LOCAL_FOLDER);
          	LocalFileController.storeFile(DEFAULT_LOCAL_FOLDER, file);
          }
    }
}
