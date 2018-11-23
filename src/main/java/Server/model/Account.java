package Server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
    @NamedQuery(
            name = "findAccountByName",
            query = "SELECT acct FROM Account acct where WHERE acct.name LIKE :userName;"
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
	
	
	private Account(String username,String password) {
		// TODO Auto-generated constructor stub
		this.username=username;
		this.password=password;
		
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
