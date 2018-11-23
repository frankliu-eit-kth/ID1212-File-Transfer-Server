package Server.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import Server.model.Account;

public class AccountDAO {
	 private final EntityManagerFactory emFactory;
	 private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
	 
	 
	 public AccountDAO() {
	        emFactory = Persistence.createEntityManagerFactory("jpaUnit");
	    }
	 
	 public Account FindAccountByName(String userName,boolean endTransactionAfterSearching) {
		 if (userName == null) {
	            return null;
	        }

	        try {
	            EntityManager em = beginTransaction();
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
	 
	 
	 private EntityManager beginTransaction() {
	        EntityManager em = emFactory.createEntityManager();
	        threadLocalEntityManager.set(em);
	        EntityTransaction transaction = em.getTransaction();
	        if (!transaction.isActive()) {
	            transaction.begin();
	        }
	        return em;
	    }

	    private void commitTransaction() {
	        threadLocalEntityManager.get().getTransaction().commit();
	    }
}
