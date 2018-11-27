package Server.model;

import java.io.File;
import java.util.HashMap;

public class FileWarehouse {
	public static HashMap<String,File> receivingStorage=new HashMap<String,File>();
	
	public static File getFile(String filename) {
		return receivingStorage.get(filename);
	}
	
	public static boolean putFile(String filename,File file) {
		if(receivingStorage.get(filename)!=null) {
			System.out.println("file already exists in warehouse");
			return false;
		}
		receivingStorage.put(filename, file);
		return true;
	}
}
