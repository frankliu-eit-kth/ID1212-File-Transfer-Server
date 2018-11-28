package Client.net;

import java.io.File;
/**
 * same old role
 * new function: pass file to the view
 * @author Frank
 *
 */
public interface OutputHandler {
	
	public void handleMsg(String msg);
	
	public void handleFile(File file);
}
