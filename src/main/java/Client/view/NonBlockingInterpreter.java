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
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private final RemoteClient myRemoteObj;
    private final OutputHandler localOutputHandler;
    private RemoteServer remoteServer;
    private long myIdAtServer;
    private boolean receivingCmds = false;
    private HashMap<String,File> fileStorage=new HashMap<String,File>();
    private NetworkController netController;
    private final int SERVER_PORT=8080;

    public NonBlockingInterpreter() throws RemoteException {
    	//why remote-> send to server,invoked by server
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
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        while (receivingCmds) {
        	String username=null;
        	String password=null;
        	String filename=null;
        	Credentials credentials=null;
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                /*
                    case QUIT:
                        receivingCmds = false;
                        server.leaveConversation(myIdAtServer);
                        boolean forceUnexport = false;
                        UnicastRemoteObject.unexportObject(myRemoteObj, forceUnexport);
                        break;
                    case LOGIN:
                        lookupServer(cmdLine.getParameter(0));
                        myIdAtServer
                                = server.login(myRemoteObj,
                                               new Credentials(cmdLine.getParameter(1),
                                                               cmdLine.getParameter(2)));
                        break;
                    case USER:
                        server.changeNickname(myIdAtServer, cmdLine.getParameter(0));
                        break;
                    default:
                        server.broadcastMsg(myIdAtServer, cmdLine.getUserInput());
                }
                */
                case REMOVE:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
                	}
                	filename= cmdLine.getParameter(0);
                	if(!remoteServer.checkFileExists(filename)) {
                		System.out.println("file does not exist");
                		break;
                	}
                	if(remoteServer.checkFilePermission(myIdAtServer,filename).equals("read")) {
                		System.out.println("you do not have permission to remove file");
                		break;
                	}
                	else {
                		if(remoteServer.removeFile(filename)) {
                			System.out.println("successfully remove");
                			break;
                		}else {
                			System.out.println("remove failed");
                			break;
                		}
                		
                	}
                case UPDATE:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
                	}
                	break;
                case STORE:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
                	}
                	filename= cmdLine.getParameter(0);
                	System.out.println(filename);
                	String url=cmdLine.getParameter(1);
                	File file= LocalFileController.readFile(url);
                
                	if(file==null) {
                		System.out.println("wrong directory, please try again");
                		break;
                	}
                	if(remoteServer.checkFileExists(filename)) {
                		System.out.println("file already exists, please choose update command");
                		break;
                	}
                	netController.sendFile(file, localOutputHandler);
                	remoteServer.storeFile(this.myIdAtServer,filename);
                	break;
                case PERMISSION:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
                	}
                	filename= cmdLine.getParameter(0);
                	String permission=cmdLine.getParameter(1);
                	if(!(permission.equals("read")||permission.equals("wirte"))){
                		System.out.println("illegal permission");
                		break;
                	}
                	if(!remoteServer.checkFileExists(filename)) {
                		System.out.println("file does not exist, please check the file name");
                		break;
                	}
                	if(!remoteServer.checkFileOwner(myIdAtServer, filename)) {
                		System.out.println("you have no right to update this file");
                		break;
                	}else {
                		if(remoteServer.changePermission(filename,permission)) {
                			System.out.println("update successful！");
                		}else {
                			System.out.println("update failed");
                		}
                			
                	}
                	break;
                	
                case CONNECT:
                	String host=cmdLine.getParameter(0);
                	lookupServer(host);
                	System.out.println(host);
                	netController.connect(host, SERVER_PORT, localOutputHandler);
                	System.out.println("successful connected to "+ remoteServer.SERVER_NAME_IN_REGISTRY);
                	break;
                case REGISTER:
                	//lookupServer(cmdLine.getParameter(0));
                	username=cmdLine.getParameter(0);
                	password=cmdLine.getParameter(1);
                	if(myIdAtServer!=0) {
                		System.out.println("you have already logged in, please log out");
                		break;
                		
                	}
                	if(remoteServer.checkUserExists(username)) {
                		System.out.println("user exists, please retry");
                		break;
                	}
                	credentials=new Credentials(username,password);
                	long newUserId=remoteServer.register(credentials);
                	outMgr.println("welcome "+username+" ! You've registered! Your user Id is "+ newUserId);
                	break;
                case LOGIN:
                	//lookupServer(cmdLine.getParameter(0));
                	username=cmdLine.getParameter(0);
                	password=cmdLine.getParameter(1);
                	//System.out.println("Test: username "+username);
                	
                	if(myIdAtServer!=0) {
                		System.out.println("you have already logged in, please log out");
                		break;
                		
                	}
                	if(!remoteServer.checkUserExists(username)) {
                		System.out.println("user does not exist, please retry");
                		break;
                	}
                	credentials=new Credentials(username,password);
                	this.myIdAtServer=remoteServer.login(myRemoteObj, credentials);
                	if(myIdAtServer==0) {
                		System.out.println("user name does not match the password, please try again");
                		break;
                	}
                	outMgr.println("welcome "+username+" ! You've logged in! Your user Id is"+ this.myIdAtServer);
                	break;
                case LIST_ALL:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
                	}
                	remoteServer.listAll(myRemoteObj);
                	break;
                	
                case QUIT:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
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

    private void lookupServer(String host) throws NotBoundException, MalformedURLException,
                                                  RemoteException {
    	//look up for server in registry
    	//get the stub
        remoteServer = (RemoteServer) Naming.lookup(
                "//" + host + "/" + RemoteServer.SERVER_NAME_IN_REGISTRY);
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private class RemoteConsoleOutput extends UnicastRemoteObject implements RemoteClient{

        public RemoteConsoleOutput() throws RemoteException {
        }
        
        @Override
        public void notify(String msg) {
        	outMgr.println(msg);
        }
       
        
      
    }
    
    private class localConsoleOutput implements OutputHandler{
    	  @Override
          public void handleMsg(String msg) {
              outMgr.println((String) msg);
          }
          
          @Override
          public void handleFile(File file) {
          	String filename=file.getName();
          	outMgr.println(filename+" received");
          	fileStorage.put(filename, file);
          }
    }
}
