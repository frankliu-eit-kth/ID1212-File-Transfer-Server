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

import Common.SerializableCredentials;

@NamedQueries({
	/**
	 * 
	 * @author Frank
	 * @debug:
	 *		NameQuery xxxx not found: check the class name in persistence.xml
	 *		Syntax error" check the syntax
	 *		there must be a versionNum field in the class if lockMode is set
	 */
    @NamedQuery(
            name = "findAccountByName",
            query = "SELECT acct FROM Account acct WHERE acct.username LIKE :userName",
            lockMode = LockModeType.OPTIMISTIC
    )
})

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
	
	private Account() {
		super();
	}
	public Account(SerializableCredentials credentials) {
		// TODO Auto-generated constructor stub
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
