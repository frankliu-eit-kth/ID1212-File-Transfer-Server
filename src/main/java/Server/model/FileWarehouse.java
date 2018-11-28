package Server.model;

import java.io.File;
import java.net.Socket;
import java.util.HashMap;
/**
 * an utility to connect file transfer server and remote controller
 * when receiving: the remote controller registers a file's name -> file transfer server receives and stores the file-> 
 * 					remote controller reads the file info, store file info in db and store the file in local folder
 * when sending: the remote controller gets the file url from database,  read the file, store in sending storage->
 * 				 the server unwrap the file request and gets the filename, then fetches the file from sending storage, then send to client
 * @author Liming Liu
 *
 */
public class FileWarehouse {
	public static HashMap<String,File> receivingStorage=new HashMap<String,File>();
	public static HashMap<String,File> sendingStorage=new HashMap<String,File>();
	
}
