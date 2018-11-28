package Server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import Common.Credentials;

@NamedQueries({
	/**
	 * 
	 * @debug
	 *		1.NameQuery xxxx not found: check the class name in persistence.xml
	 *		2.Syntax error" check the syntax
	 *				potential problems: change where->WHERE, check the character of ":"
	 *		3.there must be a versionNum field in the class if lockMode is set
	 *@concept Optimistic Lock: in low traffic, the resource will never be locked, but a thread will roll back if the resource was updated by others during its transaction
	 *			Pessimistic Lock: the resource will be locked if a thread gets it. this is used in high traffic to avoid too many roll backs
	 */
    @NamedQuery(
            name = "findAccountByName",
            query = "SELECT acct FROM Account acct WHERE acct.username LIKE :userName",
            lockMode = LockModeType.OPTIMISTIC
    ),
    @NamedQuery(
            name = "findAccountById",
            query = "SELECT acct FROM Account acct WHERE acct.userId LIKE :id",
            lockMode = LockModeType.OPTIMISTIC
    )
})
/**
 * 
 * A JPA entity which defines the account information
 * -id is generated auto in database
 * -other fields can be persisted in the program
 * 
 * Set the "javax.persistence.schema-generation.database.action" in the xml file to "nothing" so that JPA will not change the file table in database, 
 * change to "create-and-drop" the old table will be deleted and new one will be created
 * 
 * 
 * @author Liming Liu
 *
 */
@Entity(name = "Account")
public class Account {
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long userId;
	
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	 @Version
	 @Column(name = "OPTLOCK")//for optimistic lock
	 private int versionNum;
	 
	public Account() {
		
	}
	public Account(Credentials credentials) {
		this.username=credentials.getUsername();
		this.password=credentials.getPassword();
	}
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	@Override
	public String toString() {
		return "Account [userId=" + userId + ", username=" + username + ", password=" + password + "]";
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
