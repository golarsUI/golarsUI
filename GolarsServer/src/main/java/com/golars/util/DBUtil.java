package com.golars.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
			password = new String(Base64.getEncoder().encode(password.getBytes()) );
			if (password.equals(user.getPassword())) {
				trx.commit();
				session.close();
				return user;
			}

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

	public User register(User userObj) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, userObj.getUsername());
			if (user != null)
				return null;
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
			return userObj;
		} catch (Exception exception) {
			System.out.println("Exception occred while register: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		}
	}
	public User editUser(User userObj) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, userObj.getUsername());
			if(!userObj.getPassword().equals(user.getPassword()))
			userObj.setPassword(new String(Base64.getEncoder().encode(userObj.getPassword().getBytes())));
//			userObj.setNewlyCreated(user.isNewlyCreated());
//			boolean newleyCreated = user.isNewlyCreated();
			user.setFirstName(userObj.getFirstName());
			user.setLastName(userObj.getLastName());
			user.setActive(userObj.isActive());
			user.setAdmin(userObj.isAdmin());
			user.setEmailAddress(userObj.getEmailAddress());
			user.setPassword(userObj.getPassword());
//			user.setNewlyCreated(newleyCreated);
			session.update(user);
			String foldername="";
			if (userObj.getPermissonFolderID() != null && !userObj.getPermissonFolderID().equalsIgnoreCase("")){
				//claring all the permissions
				Query query = null;
				List<Folder> lst = null;
				query = session.createNativeQuery("SELECT * FROM folder f where f.userName LIKE :username", Folder.class);
				query.setString("username", "%&&&***&&&"+ userObj.getUsername() +"&&&***&&&%");
				lst =query.list();
				if(lst.size()>0)
				{
					for (Folder folder : lst) {
						folder.setUsername(folder.getUsername().replaceAll("&&&\\*\\*\\*&&&"+ userObj.getUsername() +"&&&\\*\\*\\*&&&", ""));
//						query = session.createNativeQuery("UPDATE folder f set f.userName =: where username f.userName =:username");
//						query.setString("username", folder.getUsername());
//						query.executeUpdate();
						session.update(folder);
						
					}
					
					
				}
				for (String folderId : userObj.getPermissonFolderID().split(",")) {
					
				
					int id = Integer.parseInt(folderId);

					Folder folder = (Folder) session.get(Folder.class, id);
					foldername+=folder.getLabel()+",";
					folder.setUsername(folder.getUsername() + "&&&***&&&" + userObj.getUsername() + "&&&***&&&");
					session.update(folder);

				}
			}
			if(foldername.length()>0){
				if(foldername.endsWith(",")){
					foldername = foldername.substring(0, foldername.lastIndexOf(","));
				}
				user.setPermissonFolderID(foldername);
				session.update(user);
			}
			trx.commit();
			session.close();
			return userObj;
		} catch (Exception exception) {
			System.out.println("Exception occred while update: " + exception.getMessage());
			exception.printStackTrace();
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
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

	public boolean resetPassword(ChangePassword changePasswordObj) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, changePasswordObj.getUsername());

			user.setPassword(new String(Base64.getEncoder().encode(changePasswordObj.getUpdatedPassword().getBytes())));
			session.update(user);
			trx.commit();
			session.close();
			return true;

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
			file.setFolderId(folder.getId());
			file.setParentId(folder.getParentid());
			Folder docFolder = createDocFolder(folder, fileName, documentProperties);
			trx = session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM document d where d.name =:name and d.id =:folderId",
					Document.class);
			query.setString("name", fileName);
			query.setInteger("folderId", folder.getId());
			List list = query.list();
			// Object doc = session.get(Document.class, file.getFilename());
			if (list.size() == 0) {
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
			exception.printStackTrace();
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

	public Document retrieveDocument(int id, String filename) {

		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			// trx = session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM document d where d.name =:name and d.id =:folderId",
					Document.class);
			query.setString("name", filename);
			query.setInteger("folderId", id);
			List list = query.list();

			Document doc = (Document) list.get(0);
			trx.commit();
			session.close();
			return doc;
		} catch (Exception exception) {
			exception.printStackTrace();
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
								username, isadmin, false);
						folderObj.setChildren(childList);
					}
				}
				List<Folder> tempList = new ArrayList<Folder>();
				for (Folder folder : lst) {
					getAllParents(lst,tempList,session,folder);
					
				}
				lst.addAll(tempList);
				
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

	private void getAllParents(List<Folder> lst, List<Folder> tempList, Session session, Folder folder) {
		
			if(folder.getId()!=1000 && checkExists(lst,folder.getParentid())==0){
				String parentid = folder.getParentid().substring(folder.getParentid().length()-4);
				int pId = Integer.parseInt(parentid);
				Folder flder = (Folder) session.get(Folder.class, pId);
				if(!lst.contains(flder) && !tempList.contains(flder))
					tempList.add(flder);
				if(folder.getParentid().equalsIgnoreCase("1000"))
					return;
				else
					getAllParents(lst, tempList, session,flder);
					
		}
	}

	private int checkExists(List<Folder> lst, String parentid) {
		parentid = parentid.substring(parentid.length()-4);
		int pId = Integer.parseInt(parentid);
		for (Folder folder : lst) {
			
			if(folder.getId()== pId)
				return pId;
		}
		return 0;
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
			query = session.createNativeQuery("SELECT * FROM folder f where f.id = :id and isFolder=true",
					Folder.class);
			String parentID = folder.getParentid().length() > 4
					? folder.getParentid().substring(folder.getParentid().length() - 4) : folder.getParentid();
			query.setString("id", parentID);
			lst = query.list();
			Folder parentFolder = (Folder) lst.get(0);
			folder.setUsername(parentFolder.getUsername()+ "&&&***&&&" + folder.getUsername() + "&&&***&&&");

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
		List<Folder> lst = null;
		try {
			Query query = null;
			if (isadmin)
				query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId", Folder.class);
			else {
				int id = Integer.parseInt(folderId.substring(folderId.length()-4));

				Folder folder = (Folder) session.get(Folder.class, id);
				if(folder!=null && folder.getUsername().contains( username + "&&&***&&&")){
					query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId", Folder.class);
				}else{
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.parentId = :parentId  and userName LIKE :userName",
						Folder.class);
				query.setString("userName", "%" + username + "&&&***&&&%");
				}
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
				Query query = null;
				if (settings != null) {
					query = session.createNativeQuery("UPDATE settings s SET s.value =:value WHERE s.key =:key");
					query.setString("value", userPreObj.getValue());
					query.setString("key", userPreObj.getKey());
					int result = query.executeUpdate();
				} else {
					query = session.createSQLQuery("INSERT INTO  settings(`key`,`value`) values(?,?)  ");
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

	public List<Folder> retrieveSearchResults(String searchString, String username, boolean isadmin) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		Query query = null;
		List<Folder> lst = null;
		try {
			if (isadmin) {
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.isFolder=false and (f.details LIKE :details or f.name LIKE :name)",
						Folder.class);
				query.setString("details", "%" + searchString + "%");
				query.setString("name", "%" + searchString + "%");
			} else {
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.isFolder=false and userName LIKE :userName and (f.details LIKE :details or f.name LIKE :name)",
						Folder.class);
				query.setString("userName", "%" + username + "&&&***&&&%");
				query.setString("details", "%" + searchString + "%");
				query.setString("name", "%" + searchString + "%");
			}

			// query.setBoolean("isFolder", true);
			lst = query.list();
			// if (!isadmin) {
			// for (Folder folderObj : lst) {
			// if (folderObj.getId() != 1000) {
			// List<Folder> childList =
			// retrieveSpecificFolders(folderObj.getParentid() +
			// folderObj.getId(),
			// username, true, false);
			// folderObj.setChildren(childList);
			// }
			// }
			// }
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

	public User checkUserPresent(String emailAddress) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			;
//			Transaction t = session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM user where emailAddress =:emailAddress", User.class);
			query.setString("emailAddress", emailAddress);
			List<User> lst = query.list();
			trx.commit();
			session.close();
			if (lst.size() == 1)
				return lst.get(0);
		} catch (Exception exception) {
			System.out.println("Exception occred while checkUserPresent for reset password: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		} finally {

		}
		return null;
	}

	public int updateDocumentProperties(String docId, String docName, String properties) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		Query query = null;
		int result = 0;
		try {
				query = session.createNativeQuery(
						"update folder f set f.details =:properties where f.parentId=:parentId and f.name =:name",
						Folder.class);
				query.setString("properties", properties);
				query.setString("parentId", docId);
				query.setString("name", docName);

				result = query.executeUpdate();
			
			trx.commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occred while retrieveAllFolders : " + e.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
		}
		return result;
	}

}
