package Server.net;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

import Common.Message;
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
	            	Object obj=fromClient.readObject();
	            	if(obj.getClass()==File.class) {
		                File file= (File)fromClient.readObject();
		                String filename=file.getName();
		                if(!FileWarehouse.putFile(filename, file)) {
		                	throw new Exception();
		                }else {
		                	System.out.println("test： "+FileWarehouse.receivingStorage.toString());
		                }
	            	}else {
	            		if(obj.getClass()==Message.class) {
	            			Message msg=(Message)obj;
	            			String filename=msg.getMsg();
	            			while(FileWarehouse.sendingStorage.get(filename)==null) {
	            				
	            			}
	            			File file=FileWarehouse.sendingStorage.get(filename);
	            			sendFile(file);
	            		}
	            	}
	            } catch (Exception e) {
	                disconnectClient();
	            }
	        }

	}
	
	private void sendFile(File file) {
		try {
			toClient.writeObject(file);
			toClient.flush();
			toClient.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
