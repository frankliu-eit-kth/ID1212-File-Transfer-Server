package Client.controller;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import Client.net.OutputHandler;
import Client.net.ServerConnection;

public class NetworkController {
	private final ServerConnection serverConnection = new ServerConnection();
	
	public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }
	
	public void sendFile(File file,OutputHandler outputHandler) {
		try {
			serverConnection.sendFile(file);
		}catch(Exception e) {
			outputHandler.handleMsg("sending file failed, io exeception");
			e.printStackTrace();
		}
		outputHandler.handleMsg("sending file successful");
	}
	
	public Socket getSocket() {
		return serverConnection.getSocket();
	}
	
	public void sendFileRequest(String filename) {
		serverConnection.sendFilename(filename);
	}
}
