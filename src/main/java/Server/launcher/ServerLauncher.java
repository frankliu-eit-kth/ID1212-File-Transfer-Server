package Server.launcher;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Server.net.FileTransferServer;
import Server.net.RemoteController;

/**
 * 
 * @author Liming Liu
 *
 */
public class ServerLauncher {

	 public static void main(String[] args) {
	        try {
	            ServerLauncher launcher=new ServerLauncher();
	            //register remote controller to registry
	            launcher.parseCommandLineArgs(args);
	            launcher.startRMIServant();
	            System.out.println("Remote controller reigstered");
	            //start file transfer server
	            FileTransferServer.main(args);
	            System.out.println("File transfer server started.");
	        } catch (RemoteException | MalformedURLException e) {
	            e.printStackTrace();
	        }
	    }

	    private void startRMIServant() throws RemoteException, MalformedURLException {
	        try {
	        	//returns a reference to the registry on localhost 1099
	            LocateRegistry.getRegistry().list();
	        } catch (RemoteException noRegistryRunning) {
	        	//create a new registry
	            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
	        }
	        
	        RemoteController contr = new RemoteController();
	        //bind the server name with the controller reference
	        Naming.rebind(RemoteController.SERVER_NAME_IN_REGISTRY, contr);
	    }

	    private void parseCommandLineArgs(String[] args) {
	        if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
	            System.out.println("java jpa.Server [bank name in rmi registry]");
	            System.exit(1);
	        }

	    }

}
