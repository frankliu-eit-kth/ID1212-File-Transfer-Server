package Client.net;

import java.io.File;
/**
 * same old role
 * new function: pass file to the view
 * @author Liming Liu
 *
 */
public interface OutputHandler {
	public void setFileFolder(String fileFolder);
	
	public void handleMsg(String msg);
	
	public void handleFile(File file);
}
