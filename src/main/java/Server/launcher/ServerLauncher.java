package Server.launcher;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Server.controller.Controller;


public class ServerLauncher {

	 public static void main(String[] args) {
	        try {
	            ServerLauncher launcher=new ServerLauncher();
	            launcher.parseCommandLineArgs(args);
	            launcher.startRMIServant();
	            System.out.println("server started.");
	        } catch (RemoteException | MalformedURLException e) {
	            e.printStackTrace();
	        }
	    }

	    private void startRMIServant() throws RemoteException, MalformedURLException {
	        try {
	            LocateRegistry.getRegistry().list();
	        } catch (RemoteException noRegistryRunning) {
	            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
	        }
	        Controller contr = new Controller();
	        Naming.rebind(Controller.SERVER_NAME_IN_REGISTRY, contr);
	    }

	    private void parseCommandLineArgs(String[] args) {
	        if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
	            System.out.println("java jpa.Server [bank name in rmi registry]");
	            System.exit(1);
	        }

	    }

}
