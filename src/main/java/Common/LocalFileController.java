package Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
/**
 * a file operating utility
 * @author m1339
 *
 */
public class LocalFileController {
	
	public static void makeDir(String dir) {
		try {
			new File(dir).mkdirs();
		} catch (Exception e) {
			System.out.println("LocalFileController: making directory error");
			e.printStackTrace();
		}
	}
	
	public static File readFile(String uri) {
		File file=null;
		try {
			file=new File(uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("LocalFileController: reading file error");
			e.printStackTrace();
		}
		return file;
	}
	
	public static boolean deleteFile(String uri) {
		 File file = new File(uri);
	        if(file.delete()){
	            //System.out.println(uri+" File deleted");
	            return true;
	        }else {
	        	System.out.println("LocalFileController: deleting "+uri+" failed");
	        	return false;
	        }
	}
	
	public static void updateFile(String dirName,File file) {
		storeFile(dirName,file);
	}
	
	public static boolean storeFile(String dirName, File file) {
		makeDir(dirName);
		File outFile = new File (dirName, file.getName());
		try {
            try (FileInputStream fis = new FileInputStream(file);
                 FileOutputStream fos = new FileOutputStream(outFile)) {
                FileChannel inChannel = fis.getChannel();
                FileChannel outChannel = fos.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
            }
            return true;
        } catch (IOException e) {
        	System.out.println("LocalFileController: store file error");
            e.printStackTrace();
            return false;
        }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalFileController thisController=new LocalFileController();
		File file=thisController.readFile("C:\\Software\\Java\\test directory\\test.txt");
		System.out.println(file.length());
		thisController.storeFile("C:\\Software\\Java\\test directory 1\\", file);
	}

}
