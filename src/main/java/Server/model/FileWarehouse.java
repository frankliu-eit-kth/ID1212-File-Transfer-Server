package Server.model;

import java.io.File;
import java.util.HashMap;

public class FileWarehouse {
	public static HashMap<String,File> storage;
	
	public static File getFile(String filename) {
		return storage.get(filename);
	}
	
	public static boolean putFile(String filename,File file) {
		if(storage.get(filename)!=null) {
			System.out.println("file already exists in warehouse");
			return false;
		}
		storage.put(filename, file);
		return true;
	}
}
