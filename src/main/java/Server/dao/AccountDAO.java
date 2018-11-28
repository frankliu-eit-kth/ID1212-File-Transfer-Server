package Server.dao;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import Common.Credentials;
import Server.model.Account;
import Server.model.FileMeta;
/**
 * 
 * @author Liming Liu
 *
 */
public class AccountDAO {
	/**
	 * use eneity manager factory create entity manager
	 */
	 private final EntityManagerFactory emFactory;
	 /**
	  * each thread create and store its own entity manager in a ThreadLocal<EntityMangaer>
	  * set(em) to store
	  * get() to fetch and use
	  */
	 private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
	 
	 /**
	  * load entity manager factory
	  *	 unit(connection to database) been configured in persistence.xml
	  *   connection handled by the framework
	  */
	 public AccountDAO() {
		
	        emFactory = Persistence.createEntityManagerFactory("jpaUnit");
	 }
	 private EntityManager createNewManagerAndStartTransaction() {
	        EntityManager em = emFactory.createEntityManager();
	        threadLocalEntityManager.set(em);
	        EntityTransaction transaction = em.getTransaction();
	        if (!transaction.isActive()) {
	            transaction.begin();
	        }
	        return em;
	  }
	/**
	* @question: why not use em directly in FindAccountByName
	* @answer: the transaction commits after the finally, need to re-fetch the em
	*/
	private void commitTransaction() {
	   
		threadLocalEntityManager.get().getTransaction().commit();
	  }
	 public long persistNewAccount(Credentials credentials) {
		 String username=credentials.getUsername();
		 String password=credentials.getPassword();
		 if(username==null||password==null) {
			 System.out.println("username or password illegal");
			 return 0;
		 }

	        try {
	           EntityManager em = createNewManagerAndStartTransaction();
	           em.persist(new Account(credentials));
	           Account persistedAccount=em.createNamedQuery("findAccountByName",Account.class).setParameter("userName", credentials.getUsername()).getSingleResult();
	           return persistedAccount.getUserId();
	            
	        } finally {
	              commitTransaction();
	        }
	 }
	 
	 public Account FindAccountByName(String userName,boolean endTransactionAfterSearching) {
		 if (userName == null) {
	            return null;
	        }

	        try {
	            EntityManager em = createNewManagerAndStartTransaction();
	            try {
	                return em.createNamedQuery("findAccountByName", Account.class).
	                        setParameter("userName", userName).getSingleResult();
	            } catch (NoResultException noSuchAccount) {
	                return null;
	            }
	        } finally {
	            if (endTransactionAfterSearching) {
	              commitTransaction();
	            }
	        }
	 }
	 
	 public Account FindAccountById(long id,boolean endTransactionAfterSearching) {
		 if (id == 0) {
	            return null;
	        }

	        try {
	            EntityManager em = createNewManagerAndStartTransaction();
	            try {
	                return em.createNamedQuery("findAccountById", Account.class).
	                        setParameter("id", id).getSingleResult();
	            } catch (NoResultException noSuchAccount) {
	                return null;
	            }
	        } finally {
	            if (endTransactionAfterSearching) {
	              commitTransaction();
	            }
	        }
	 }
	 
}
