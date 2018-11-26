package Client.net;

import java.io.File;

public interface OutputHandler {
	public void handleMsg(String msg);
	
	public void handleFile(File file);
}
