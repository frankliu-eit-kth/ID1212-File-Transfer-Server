package Common;

import java.io.Serializable;
/**
 * a serializable wrapper for string message
 * @author Liming Liu
 *
 */
public class Message implements Serializable {
	private String msg;
	
	public Message(String msg) {
		this.msg=msg;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
