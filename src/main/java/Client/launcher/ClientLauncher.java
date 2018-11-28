package Client.launcher;

import java.rmi.RemoteException;

import Client.view.NonBlockingInterpreter;

public class ClientLauncher {
/**
 * same old
 * @author Frank
 * @param args
 * @throws RemoteException
 */
	public static void main(String[] args) throws RemoteException {
		try {
            new NonBlockingInterpreter().start();
        } catch (RemoteException ex) {
            System.out.println("Could not start client.");
        }
	}

}
