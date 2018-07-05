package com.golars.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.golars.bean.Document;
import com.golars.bean.Folder;
import com.golars.bean.User;



public class DBUtil {
	public User login(String username, String password) {
		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, username);
			password = new String(Base64.getEncoder().encode(password.getBytes()));
			if (password.equals(user.getPassword())) {
				System.out.println("User: " + user.toString());
				return user;
			}
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			tx1.rollback();

			return null;
		} finally {
			tx1.commit();
			session.close();
		}
		return null;
	}

	public boolean register(User userObj) {
		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			userObj.setPassword(new String(Base64.getEncoder().encode(userObj.getPassword().getBytes())));
			String abc = (String) session.save(userObj);
			return true;
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			tx1.rollback();
			return false;
		} finally {
			tx1.commit();
			session.close();
		}
	}

	public List<User> getAllUsers() {
		
		 Session session = HibernateUtil.getSession();;
         Transaction t = session.beginTransaction();
         Query query = session.createNativeQuery("SELECT * FROM user",User.class);
         List lst = query.list();
         session.close();
		return lst;
	}

	public boolean saveDocument(InputStream is, String fileName, String documentProperties, Folder folder) {
		Session session = HibernateUtil.getSession();
		byte[] theString;
		Transaction tx1 = null;
		try {
			theString = IOUtils.toByteArray(is);
		 
			Document file = new Document();
		file.setFilename(fileName);
		file.setContent(theString);
		file.setParentId(folder.getParentid());
		file.setProperties(documentProperties);
		Folder docFolder = createDocFolder(folder,fileName);
		tx1 = session.beginTransaction();
		
		session.save(docFolder);// saving doc in folder table
		String abc = (String) session.save(file);// save doc content in other table
			return true;
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			if(tx1!=null)
			tx1.rollback();
			return false;
		} finally {
			if(tx1!=null)
			tx1.commit();
			session.close();
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		
		// TODO Auto-generated method stub
		
	}

	private Folder createDocFolder(Folder folder,String docName) {
		Folder docFolder = new Folder();
		docFolder.setLabel(docName);
		docFolder.setParentid(folder.getParentid()+folder.getId());
		docFolder.setUsername(folder.getUsername());
		return docFolder;
	}

	public Document retrieveDocument(String filename) {
		
		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			Document doc = (Document) session.get(Document.class, filename);
			return doc;
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			tx1.rollback();

			return null;
		} finally {
			tx1.commit();
			session.close();
		}		
	}

	public List<Folder> retrieveAllFolders() {
		 Session session = HibernateUtil.getSession();;
         Transaction t = session.beginTransaction();
         Query query = session.createNativeQuery("SELECT * FROM folder f where f.isFolder=:isFolder",Folder.class);
         query.setBoolean("isFolder", true);
         List lst = query.list();
         session.close();
		return lst;
	}

	public Folder createFolder(Folder folder) {

			Session session = HibernateUtil.getSession();
			Transaction tx1 = session.beginTransaction();
			try {
				int abc = (Integer)session.save(folder);
				session.flush();
				 Query query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId and f.name= :folderName",Folder.class);
				 query.setString("parentId", folder.getParentid());
				 query.setString("folderName", folder.getLabel());
				 List lst = query.list();
				return (Folder) lst.get(0);
			} catch (Exception exception) {
				System.out.println("Exception occred while reading user data: " + exception.getMessage());
				tx1.rollback();
				return null;
			} finally {
				tx1.commit();
				session.close();
			}
		}
	public List<Folder> retrieveSpecificFolderWithParentIdAndName(String folderId) {
		Session session = HibernateUtil.getSession();;
        Transaction t = session.beginTransaction();
        Query query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId",Folder.class);
        query.setString("parentId", folderId);
        List lst = query.list();
        session.close();
		return lst;
	}


		public List<Folder> retrieveSpecificFolders(String folderId) {
			Session session = HibernateUtil.getSession();;
	        Transaction t = session.beginTransaction();
	        Query query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId",Folder.class);
	        query.setString("parentId", folderId);
	        List lst = query.list();
	        session.close();
			return lst;
		}

		public String fetchDocDetails(String docName) {
			 Session session = HibernateUtil.getSession();;
	         Transaction t = session.beginTransaction();
	         Query query = session.createNativeQuery("SELECT details FROM document s where s.name=:filename");
	         query.setString("filename", docName);
	         List lst = query.list();
	         session.close();
			return (String) lst.get(0);
		
		}

		public int deleteFolder(String folderId, String parentId) {
			Session session = HibernateUtil.getSession();;
	        Transaction t = session.beginTransaction();
	        Query query = session.createNativeQuery("DELETE FROM folder WHERE id=:id OR parentId LIKE :parentId",Folder.class);
	        query.setInteger("id", Integer.parseInt(folderId));
	        query.setString("parentId", parentId+folderId+"%");
	        int result = query.executeUpdate();
	        
	        session.close();
			return result;
			
		}
		
	
}
