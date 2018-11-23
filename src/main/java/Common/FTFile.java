package Common;

import java.io.File;
import java.io.Serializable;

public class FTFile implements Serializable {
	private String filename;
	private int size;
	private String owner;
	private String permission;
	private String url;
	private File fileContent;
	
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public File getFileContent() {
		return fileContent;
	}

	public void setFileContent(File fileContent) {
		this.fileContent = fileContent;
	}

	public FTFile(String filename) {
		// TODO Auto-generated constructor stub
		this.filename=filename;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FTFile testFile=new FTFile("test.txt");
		testFile.setUrl("test.txt");
		testFile.setFileContent(new File(testFile.url));
		System.out.println(testFile.fileContent.length());
	}

}
