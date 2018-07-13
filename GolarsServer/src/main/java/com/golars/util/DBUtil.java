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
import com.golars.bean.UserSettings;

public class DBUtil {
	public User login(String username, String password) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, username);
			password = new String(Base64.getEncoder().encode(password.getBytes()));
			if (password.equals(user.getPassword())) {
				System.out.println("User: " + user.toString());
				return user;
			}
			trx.commit();
			session.close();
		} catch (Exception exception) {
			System.out.println("Exception occred while login: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		} finally {

		}
		return null;
	}

	public boolean register(User userObj) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, userObj.getUsername());
			if (user != null)
				return false;
			userObj.setPassword(new String(Base64.getEncoder().encode(userObj.getPassword().getBytes())));
			session.save(userObj);
			if (userObj.getPermissonFolderID() != null && !userObj.getPermissonFolderID().equalsIgnoreCase(""))
				for (String folderId : userObj.getPermissonFolderID().split(",")) {
					int id = Integer.parseInt(folderId);

					Folder folder = (Folder) session.get(Folder.class, id);
					folder.setUsername(folder.getUsername() + "&&&***&&&" + userObj.getUsername() + "&&&***&&&");
					session.update(folder);

				}
			trx.commit();
			session.close();
			return true;
		} catch (Exception exception) {
			System.out.println("Exception occred while register: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return false;
		}
	}

	public boolean changePassword(ChangePassword changePasswordObj) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, changePasswordObj.getUsername());

			String password = new String(Base64.getEncoder().encode(changePasswordObj.getPassword().getBytes()));
			if (password.equals(user.getPassword())) {
				user.setPassword(
						new String(Base64.getEncoder().encode(changePasswordObj.getUpdatedPassword().getBytes())));
				user.setNewlyCreated(false);
				session.update(user);
				trx.commit();
				session.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			System.out.println("Exception occred while changePassword: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return false;
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
		Transaction trx = null;
		try {
			theString = IOUtils.toByteArray(is);

			Document file = new Document();
			file.setFilename(fileName);
			file.setContent(theString);
			file.setParentId(folder.getParentid());
			Folder docFolder = createDocFolder(folder, fileName, documentProperties);
			trx = session.beginTransaction();
			Object doc = session.get(Document.class, file.getFilename());
			if (doc == null) {
				session.save(docFolder);// saving doc in folder table
				session.save(file);// save doc content in
									// other table
				trx.commit();
				session.close();
			} else {
				trx.rollback();
				;
				session.close();
				return false;
			}
			return true;
		} catch (Exception exception) {
			System.out.println("Exception occred while saveDocument: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
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
		docFolder.setUsername(folder.getUsername() + "&&&***&&&");
		docFolder.setProperties(documentProperties);
		return docFolder;
	}

	public Document retrieveDocument(String filename) {

		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			Document doc = (Document) session.get(Document.class, filename);
			trx.commit();
			session.close();
			return doc;
		} catch (Exception exception) {
			System.out.println("Exception occred while retrieveDocument: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		} finally {

		}
	}

	public List<Folder> retrieveAllFolders(String username, boolean isadmin) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		Query query = null;
		List<Folder> lst = null;
		try {
			if (isadmin)
				query = session.createNativeQuery("SELECT * FROM folder f where f.isFolder=:isFolder", Folder.class);
			else {
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.isFolder=:isFolder and userName LIKE :userName or f.id=1000",
						Folder.class);
				query.setString("userName", "%" + username + "&&&***&&&%");
			}

			query.setBoolean("isFolder", true);
			lst = query.list();
			if (!isadmin) {
				for (Folder folderObj : lst) {
					if (folderObj.getId() != 1000) {
						List<Folder> childList = retrieveSpecificFolders(folderObj.getParentid() + folderObj.getId(),
								username, true, false);
						folderObj.setChildren(childList);
					}
				}
			}
			trx.commit();
			session.close();
		} catch (Exception e) {
			System.out.println("Exception occred while retrieveAllFolders : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		return lst;
	}

	public Folder createFolder(Folder folder) {

		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			// checking for folder existence

			Query query = session.createNativeQuery(
					"SELECT * FROM folder f where f.parentId = :parentId and f.name= :folderName", Folder.class);
			query.setString("parentId", folder.getParentid());
			query.setString("folderName", folder.getLabel());
			List lst = query.list();
			if (lst.size() > 0)
				return null;
			folder.setUsername(folder.getUsername() + "&&&***&&&");
			int abc = (Integer) session.save(folder);
			session.flush();
			query = session.createNativeQuery(
					"SELECT * FROM folder f where f.parentId = :parentId and f.name= :folderName", Folder.class);
			query.setString("parentId", folder.getParentid());
			query.setString("folderName", folder.getLabel());
			lst = query.list();
			trx.commit();
			session.close();
			return (Folder) lst.get(0);
		} catch (Exception e) {
			System.out.println("Exception occred while retrieveSpecificFolderWithParentIdAndName : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		} finally {

		}
		return null;
	}

	public List<Folder> retrieveSpecificFolderWithParentIdAndName(String folderId) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		List lst = null;
		try {
			Query query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId",
					Folder.class);
			query.setString("parentId", folderId);
			lst = query.list();
			trx.commit();
			session.close();
		} catch (Exception e) {
			System.out.println("Exception occred while retrieveSpecificFolderWithParentIdAndName : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		return lst;
	}

	public List<Folder> retrieveSpecificFolders(String folderId, String username, boolean isadmin) {
		Session session = HibernateUtil.getSession();
		;
		Transaction t = session.beginTransaction();
		List lst = null;
		try {
			Query query = null;
			if (isadmin || !folderId.equalsIgnoreCase("1000"))
				query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId", Folder.class);
			else {
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.parentId = :parentId  and userName LIKE :userName",
						Folder.class);
				query.setString("userName", "%" + username + "&&&***&&&%");
			}
			query.setString("parentId", folderId);
			lst = query.list();
			t.commit();
			session.close();
		} catch (Exception e) {
			t.rollback();
			session.close();
		}
		return lst;
	}

	public List<Folder> retrieveSpecificFolders(String folderId, String username, boolean isadmin,
			boolean docRequired) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		Query query = null;
		List lst = null;
		try {
			if (isadmin)
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.parentId = :parentId and f.isFolder=true", Folder.class);
			else {
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.parentId = :parentId  and userName =:userName and f.isFolder=true",
						Folder.class);
				query.setString("userName", username);
			}
			query.setString("parentId", folderId);
			lst = query.list();
			trx.commit();
			session.close();

		} catch (Exception e) {
			System.out.println("Exception occred while retrieveSpecificFolders : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		return lst;
	}

	/*
	 * public String fetchDocDetails(String docName) { Session session =
	 * HibernateUtil.getSession(); ; Transaction t = session.beginTransaction();
	 * Query query = session.createNativeQuery(
	 * "SELECT details FROM document s where s.name=:filename");
	 * query.setString("filename", docName); List lst = query.list();
	 * session.close(); return (String) lst.get(0);
	 * 
	 * }
	 */

	public int deleteFolder(String folderId, String parentId, String username, boolean isadmin) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		int result = 0;
		try {
			Query query;

			if (isadmin)
				query = session.createNativeQuery("DELETE FROM folder WHERE id=:id OR parentId LIKE :parentId",
						Folder.class);
			else {
				query = session.createNativeQuery(
						"DELETE FROM folder WHERE id=:id OR parentId LIKE :parentId and username =:username",
						Folder.class);
				query.setString("username", username);
			}
			query.setInteger("id", Integer.parseInt(folderId));
			query.setString("parentId", parentId + folderId + "%");
			result = query.executeUpdate();
			trx.commit();
			session.close();
		} catch (NumberFormatException e) {
			System.out.println("Exception occred while deleteFolder : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		return result;

	}

	public List<UserSettings> retrieveUserPreferences() {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		// query.setString("preferenceName", preferenceName);
		List lst = null;
		try {
			Query query = session.createNativeQuery("SELECT * FROM settings", UserSettings.class);
			lst = query.list();
			trx.commit();
			session.close();
		} catch (Exception e) {
			System.out.println("Exception occred while retrieveUserPreferences : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		// session.close();
		return lst;
	}

	public List<User> deleteUser(String username) {
		Session session = HibernateUtil.getSession();

		Transaction trx = session.beginTransaction();
		try {
			Query query = session.createNativeQuery("DELETE FROM User WHERE userName =:username", Folder.class);
			query.setString("username", username);
			int result = query.executeUpdate();
			trx.commit();
			session.close();
		} catch (Exception e) {
			System.out.println("Exception occred while deleting user : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		List<User> userList = getAllUsers();

		return userList;

	}

	public void updatePreferences(UserSettings[] settingArray) {
		Session session = null;

		Transaction trx = null;
		try {
			// stub
			for (UserSettings userPreObj : settingArray) {
				session = HibernateUtil.getSession();

				trx = session.beginTransaction();// TODO Auto-generated method
				UserSettings settings = (UserSettings) session.get(UserSettings.class, userPreObj.getKey());
				Query	query = null;	
				if(settings!=null){
					query  = session.createNativeQuery("UPDATE settings s SET s.value =:value WHERE s.key =:key");
				query.setString("value", userPreObj.getValue());
				query.setString("key", userPreObj.getKey());
				int result = query.executeUpdate();
				}else{
					query  = session.createSQLQuery("INSERT INTO  settings(`key`,`value`) values(?,?)  ");
					query.setParameter(0, userPreObj.getKey());
					query.setParameter(1, userPreObj.getValue());
					query.executeUpdate();
				}
				trx.commit();
				session.close();
			}
		} catch (Exception exception) {
			System.out.println("Exception occred while updating user Preferences : " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();

		}
		// User user = (User) session.get(User.class, username);

	}

}
