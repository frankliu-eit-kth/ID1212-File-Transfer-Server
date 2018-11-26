package Client.net;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection {

	/*
	 * @role: timeout params
	 */
	private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    /*
     * @role:the socket used for connection
     */
    private Socket socket;
    /*
     * @role: io streams for communication with the server
     * @futhermore: hereby use the PrintWriter and BufferedReader to read text, and there are a lot kinds of other streams could be chosen from
     */
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    /*
     * @role: flag used to maintain the life of a thread
     */
    private volatile boolean connected;
  
    public void connect(String host, int port, OutputHandler broadcastHandler) throws
    IOException {
    	 socket = new Socket();
         socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
         socket.setSoTimeout(TIMEOUT_HALF_HOUR);
         connected = true;
         boolean autoFlush = true;
         toServer = new ObjectOutputStream(socket.getOutputStream());
         fromServer = new ObjectInputStream(socket.getInputStream());
         new Thread(new Listener(broadcastHandler)).start();
    }
    
    public void sendFile(File file) throws Exception{
    	
    	try {
			toServer.writeObject(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
            	connectionFailure.printStackTrace();
                if (connected) {
                    outputHandler.handleMsg("Lost connection.");
                }
            }
        }

    
        
    }

}
