package com.golars.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.golars.bean.ChangePassword;
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
			User user = (User) session.get(User.class, userObj.getUsername());
			if(user!=null)
				return false;
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

	public boolean changePassword(ChangePassword changePasswordObj) {
		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, changePasswordObj.getUsername());

			String password = new String(Base64.getEncoder().encode(changePasswordObj.getPassword().getBytes()));
			if (password.equals(user.getPassword())) {
				user.setPassword(
						new String(Base64.getEncoder().encode(changePasswordObj.getUpdatedPassword().getBytes())));
				user.setNewlyCreated(false);
				session.update(user);
				return true;
			} else {
				return false;
			}
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

		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		Query query = session.createNativeQuery("SELECT * FROM user", User.class);
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
			Folder docFolder = createDocFolder(folder, fileName,documentProperties);
			tx1 = session.beginTransaction();
			Object doc = session.get(Document.class, file.getFilename());
			if(doc==null){
			session.save(docFolder);// saving doc in folder table
			session.save(file);// save doc content in
								// other table
			tx1.commit();
			session.close();
			}else
			{
				tx1.rollback();;
				session.close();
				return false;
			}
			return true;
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			if (tx1 != null)
				tx1.rollback();
			return false;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}

		}

		// TODO Auto-generated method stub

	}

	private Folder createDocFolder(Folder folder, String docName, String documentProperties) {
		Folder docFolder = new Folder();
		docFolder.setLabel(docName);
		docFolder.setParentid(folder.getParentid() + folder.getId());
		docFolder.setUsername(folder.getUsername());
		docFolder.setProperties(documentProperties);
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

	public List<Folder> retrieveAllFolders(String username, boolean isadmin) {
		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		Query query = null;
		if (isadmin)
			query = session.createNativeQuery("SELECT * FROM folder f where f.isFolder=:isFolder", Folder.class);
		else {
			query = session.createNativeQuery(
					"SELECT * FROM folder f where f.isFolder=:isFolder and userName =:userName or f.id=1000",
					Folder.class);
			query.setString("userName", username);
		}

		query.setBoolean("isFolder", true);
		List lst = query.list();
		session.close();
		return lst;
	}

	public Folder createFolder(Folder folder)  {

		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			// checking for folder existence
			
			Query query = session.createNativeQuery(
					"SELECT * FROM folder f where f.parentId = :parentId and f.name= :folderName", Folder.class);
			query.setString("parentId", folder.getParentid());
			query.setString("folderName", folder.getLabel());
			List lst = query.list();
			if(lst.size()>0)
				return null;
			
			int abc = (Integer) session.save(folder);
			session.flush();
			query = session.createNativeQuery(
					"SELECT * FROM folder f where f.parentId = :parentId and f.name= :folderName", Folder.class);
			query.setString("parentId", folder.getParentid());
			query.setString("folderName", folder.getLabel());
			lst = query.list();
			return (Folder) lst.get(0);
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			tx1.rollback();
		} finally {
			tx1.commit();
			session.close();
		}
		return null;
	}

	public List<Folder> retrieveSpecificFolderWithParentIdAndName(String folderId) {
		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		Query query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId", Folder.class);
		query.setString("parentId", folderId);
		List lst = query.list();
		session.close();
		return lst;
	}

	public List<Folder> retrieveSpecificFolders(String folderId, String username, boolean isadmin) {
		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		Query query = null;
		if (isadmin)
			query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId", Folder.class);
		else {
			query = session.createNativeQuery(
					"SELECT * FROM folder f where f.parentId = :parentId  and userName =:userName", Folder.class);
			query.setString("userName", username);
		}
		query.setString("parentId", folderId);
		List lst = query.list();
		session.close();
		return lst;
	}

/*	public String fetchDocDetails(String docName) {
		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		Query query = session.createNativeQuery("SELECT details FROM document s where s.name=:filename");
		query.setString("filename", docName);
		List lst = query.list();
		session.close();
		return (String) lst.get(0);

	}*/

	public int deleteFolder(String folderId, String parentId) {
		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		Query query = session.createNativeQuery("DELETE FROM folder WHERE id=:id OR parentId LIKE :parentId",
				Folder.class);
		query.setInteger("id", Integer.parseInt(folderId));
		query.setString("parentId", parentId + folderId + "%");
		int result = query.executeUpdate();

		session.close();
		return result;

	}

	public String retrieveUserPreferences(String preferenceName) {
		Session session = HibernateUtil.getSession();
		Transaction t = session.beginTransaction();
		Query query = session.createNativeQuery("SELECT value FROM settings s where s.key=:preferenceName");
		query.setString("preferenceName", preferenceName);
		List lst = query.list();
		session.close();
		return (String) lst.get(0);
	}

}
