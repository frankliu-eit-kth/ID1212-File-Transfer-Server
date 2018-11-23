package Server.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import Server.model.PersistantAccount;

public class AccountOperationDAO {
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
	 public AccountOperationDAO() {
		 
	        emFactory = Persistence.createEntityManagerFactory("jpaUnit");
	    }
	 
	 public PersistantAccount FindAccountByName(String userName,boolean endTransactionAfterSearching) {
		 if (userName == null) {
	            return null;
	        }

	        try {
	            EntityManager em = createNewManagerAndStartTransaction();
	            try {
	                return em.createNamedQuery("findAccountByName", PersistantAccount.class).
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
}
