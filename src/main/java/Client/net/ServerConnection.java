package Client.net;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import Common.Message;
/**
 * mostly same old
 * @author Liming Liu
 * @role sending filename in a message package
 * 		 send/receive file through TCP blocking socket
 *
 */
public class ServerConnection {

	
	private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    
    private Socket socket;
    
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    
    private volatile boolean connected;
  
    public void connect(String host, int port, OutputHandler broadcastHandler) throws
    IOException {
    	 socket = new Socket();
         socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
         socket.setSoTimeout(TIMEOUT_HALF_HOUR);
         connected = true;
         toServer = new ObjectOutputStream(socket.getOutputStream());
         fromServer = new ObjectInputStream(socket.getInputStream());
         new Thread(new Listener(broadcastHandler)).start();
    }
    
    public void disconnect() throws IOException {
    	if(!connected) {
    		return;
    	}
    	
        socket.close();
        socket = null;
        connected = false;
    }
    
    public void sendFile(File file) throws Exception{
    	
    	try {
			toServer.writeObject(file);
		} catch (Exception e) {
			//unsolved exception but does not affect the function, temporarily skip it
			//e.printStackTrace();
		}
    }
   
    public void sendFilename(String filename) {
    	try {
    		Message msg=new Message(filename);
			toServer.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
        	
            try {
                for (;;) {
                	
                    outputHandler.handleFile((File)fromServer.readObject());
                }
            } catch (Throwable connectionFailure) {
            	//connectionFailure.printStackTrace();
                if (connected) {
                    outputHandler.handleMsg("Lost connection.");
                }
            }
            
        }
    }
}
