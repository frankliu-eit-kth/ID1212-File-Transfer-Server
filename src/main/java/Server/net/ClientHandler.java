package Server.net;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

import Common.MessageException;
import Server.model.FileWarehouse;

public class ClientHandler implements Runnable {
	private final FileTransferServer server;
    private final Socket clientSocket;
   
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    
    private boolean connected;
	public ClientHandler(FileTransferServer server, Socket clientSocket) {
		// TODO Auto-generated constructor stub
		 this.server = server;
	     this.clientSocket = clientSocket;
	     connected = true;
	}

	@Override
	public void run() {
		try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
		 while (connected) {
	            try {
	                File file= (File)fromClient.readObject();
	                String filename=file.getName();
	                if(!FileWarehouse.putFile(filename, file)) {
	                	throw new Exception();
	                }else {
	                	System.out.println("test： "+FileWarehouse.storage.toString());
	                }
	                
	            } catch (Exception e) {
	                disconnectClient();
	            }
	        }

	}
	 private void disconnectClient() {
	        try {
	            clientSocket.close();
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
	        connected = false;
	        server.removeHandler(this);
	        System.out.println("client leave");
	    }

}
