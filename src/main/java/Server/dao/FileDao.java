package Server.dao;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import Server.model.Account;
import Server.model.FileMeta;

public class FileDao {
	private final EntityManagerFactory emFactory;
	private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
	
	public FileDao() {
		
	        emFactory = Persistence.createEntityManagerFactory("jpaUnit");
	 }
	public boolean removeFile(String filename) {
		try {
			EntityManager em = createNewManagerAndStartTransaction();
			FileMeta filemeta = em.createNamedQuery("findFileByName", FileMeta.class).setParameter("filename", filename)
					.getSingleResult();
			em.remove(filemeta);
			return true;
		} finally {
			commitTransaction();
		}
	}
	public boolean checkFileExists(String filename) {
		try {
	           EntityManager em = createNewManagerAndStartTransaction();
	          
	           List<FileMeta> filemeta= em.createNamedQuery("findFileByName",FileMeta.class).setParameter("filename", filename).getResultList();
	           if(filemeta.size()==0) {
	        	   return false;
	           }
	           else {
	        	   return true;
	           }
	            
	        } finally {
	              commitTransaction();
	        }
	}
	public FileMeta findFile(String filename) {
		try {
	           EntityManager em = createNewManagerAndStartTransaction();
	          
	           FileMeta filemeta= em.createNamedQuery("findFileByName",FileMeta.class).setParameter("filename", filename).getSingleResult();
	           return filemeta;
	            
	        } finally {
	              commitTransaction();
	        }
	}
	public FileMeta findFileOwner(String filename) {
		try {
	           EntityManager em = createNewManagerAndStartTransaction();
	          
	           FileMeta filemeta= em.createNamedQuery("findFileByName",FileMeta.class).setParameter("filename", filename).getSingleResult();
	           return filemeta;
	            
	        } finally {
	              commitTransaction();
	        }
	}
	public List<FileMeta>findAll() {
		 try {
	           EntityManager em = createNewManagerAndStartTransaction();
	          
	           List<FileMeta> files= em.createNamedQuery("findAllFileMetadata",FileMeta.class).getResultList();
	           return files;
	            
	        } finally {
	              commitTransaction();
	        }
	}
	
	public void storeNewFileMeta(FileMeta file) {
		try {
			EntityManager em = createNewManagerAndStartTransaction();
            em.persist(file);
		}finally {
            commitTransaction();
        }
	}
	
	public void updateFileMeta(FileMeta file) {
		try {
			EntityManager em = createNewManagerAndStartTransaction();
			FileMeta filemeta= em.createNamedQuery("findFileByName",FileMeta.class).setParameter("filename", file.getFilename()).getSingleResult();
			filemeta.setOwner(file.getOwner());
			filemeta.setPermission(file.getPermission());
			filemeta.setSize(file.getSize());
			filemeta.setUrl(file.getUrl());
			em.merge(filemeta);
		}finally {
            commitTransaction();
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
	 private void commitTransaction() {
	        threadLocalEntityManager.get().getTransaction().commit();
	        
	    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AccountDAO acctDao=new AccountDAO();
		FileDao fileDao=new FileDao();
		Account me= acctDao.FindAccountByName("Frank", true);
		FileMeta newFile=new FileMeta("test1",me);
		fileDao.storeNewFileMeta(newFile);
		
	}

}
