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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import Client.net.CommunicationListener;
import Common.RemoteFTClient;
import Common.RemoteFTServer;
import Common.SerializableCredentials;

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
    private final RemoteFTClient myRemoteObj;
    private RemoteFTServer remoteServer;
    private long myIdAtServer;
    private boolean receivingCmds = false;

    public NonBlockingInterpreter() throws RemoteException {
    	//why remote-> send to server,invoked by server
        myRemoteObj = new ConsoleOutput();
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
        	SerializableCredentials credentials=null;
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
                case CONNECT:
                	lookupServer(cmdLine.getParameter(0));
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
                	credentials=new SerializableCredentials(username,password);
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
                	credentials=new SerializableCredentials(username,password);
                	this.myIdAtServer=remoteServer.login(myRemoteObj, credentials);
                	if(myIdAtServer==0) {
                		System.out.println("user name does not match the password, please try again");
                		break;
                	}
                	outMgr.println("welcome "+username+" ! You've logged in! Your user Id is"+ this.myIdAtServer);
                	break;
                case QUIT:
                	if(myIdAtServer==0) {
                		outMgr.println("you have not logged in");
                		break;
                	}else {
                		if(remoteServer.userLeave(myIdAtServer)) {
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
        remoteServer = (RemoteFTServer) Naming.lookup(
                "//" + host + "/" + RemoteFTServer.SERVER_NAME_IN_REGISTRY);
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput extends UnicastRemoteObject implements RemoteFTClient,CommunicationListener {

        public ConsoleOutput() throws RemoteException {
        }
        
        public void notify(String msg) {
        	outMgr.println(msg);
        }
        /**
        @Override
        public void recvMsg(String msg) {
            outMgr.println((String) msg);
        }
        */
    }
}
