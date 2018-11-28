package Client.controller;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import Client.net.OutputHandler;
import Client.net.ServerConnection;
/**
 * 
 * @author Liming Liu
 * @role provides functions invoked by view layer to complete network operations
 *
 */
public class NetworkController {
	
	private final ServerConnection serverConnection = new ServerConnection();
	/**
	 * same old
	 */
	public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }
	/**
	 * send file through TCP blocking sockets in the network layer, if succeeded notify the view layer
	 */
	public void sendFile(File file,OutputHandler outputHandler) {
		try {
			serverConnection.sendFile(file);
		}catch(Exception e) {
			outputHandler.handleMsg("sending file failed, io exeception");
			e.printStackTrace();
		}
		outputHandler.handleMsg("sending file successful");
	}
	/**
	 * send filename to the file transfer server( not remote controller), 
	 * the client handler would unpack the message, extract the filename and send file back to client
	 * @param filename
	 */
	public void sendFileRequest(String filename) {
		serverConnection.sendFilename(filename);
	}
}
