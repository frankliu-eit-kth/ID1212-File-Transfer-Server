package Server.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQueries({
	/**
	 * JPQL language
	 */
	@NamedQuery(
            name = "findAllFileMetadata",
            query = "SELECT file FROM File file"
    ),
	@NamedQuery(
            name = "findFileByName",
            query = "SELECT file FROM File file WHERE file.filename LIKE :filename"
    )
	
})
/**
 * 
 * A JPA entity which defines the file information, does not contain file content
 * -id is generated auto in database
 * -other fields( including the one-to-one relationship with Account object) will be persisted in the program
 * 
 * Set the "javax.persistence.schema-generation.database.action" in the xml file to "nothing" so that JPA will not change the file table in database, 
 * change to "create-and-drop" the old table will be deleted and new one will be created
 * 
 * @problem: cascade relationship still not very clear
 * 
 * @author Liming Liu
 *
 */
@Entity(name = "File")
public class FileMeta  {
	
	@Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)//auto generate
    private long fileId;
	
	
	@Column(name = "filename", nullable = false)
    private String filename;
	
	@Column(name = "size", nullable = false)
    private int size;
	
	 @OneToOne(cascade = CascadeType.REFRESH) //one to one mapping
	 @JoinColumn(name = "owner", nullable = false)// 
	 private Account owner;
	
	@Column(name = "url", nullable = false)
    private String url;
	
	@Column(name = "permission", nullable = false)
    private String permission;
	
	
	
	public FileMeta() {
		
	}
	
	public FileMeta(String filename,Account acct) {
		this.filename=filename;
		this.owner=acct;
	}
	
	
	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

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

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		
		return "File info [fileId=" + fileId + ", filename=" + filename + ", size=" + size + ", owner=" +owner.getUsername() 
				+ ", url=" + url + ", permission=" + permission + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
