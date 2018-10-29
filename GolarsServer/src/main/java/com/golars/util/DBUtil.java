package com.golars.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

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
	private static DBUtil dbObj;

	public DBUtil() {
	}

	public static DBUtil getInstance() {
		if (dbObj == null) {
			dbObj = new DBUtil();
		}
		return dbObj;
	}

	public User login(String username, String password) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, username);
			password = new String(Base64.getEncoder().encode(password.getBytes()));
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
					folderId = folderId.substring(0, folderId.indexOf("%%$%%"));
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
			if (!userObj.getPassword().equals(user.getPassword()))
				userObj.setPassword(new String(Base64.getEncoder().encode(userObj.getPassword().getBytes())));
			// userObj.setNewlyCreated(user.isNewlyCreated());
			// boolean newleyCreated = user.isNewlyCreated();
			user.setFirstName(userObj.getFirstName());
			user.setLastName(userObj.getLastName());
			user.setActive(userObj.isActive());
			user.setAdmin(userObj.isAdmin());
			user.setEmailAddress(userObj.getEmailAddress());
			user.setPassword(userObj.getPassword());
			// user.setNewlyCreated(newleyCreated);
			session.update(user);
			String foldername = "";
			if (userObj.getPermissonFolderID() != null && !userObj.getPermissonFolderID().equalsIgnoreCase("")) {
				// claring all the permissions
				Query query = null;
				List<Folder> lst = null;
				query = session.createNativeQuery("SELECT * FROM folder f where f.userName LIKE :username",
						Folder.class);
				query.setString("username", "%&&&***&&&" + userObj.getUsername() + "&&&***&&&%");
				lst = query.list();
				if (lst.size() > 0) {
					for (Folder folder : lst) {
						folder.setUsername(folder.getUsername()
								.replaceAll("&&&\\*\\*\\*&&&" + userObj.getUsername() + "&&&\\*\\*\\*&&&", ""));
						// query = session.createNativeQuery("UPDATE folder f
						// set f.userName =: where username f.userName
						// =:username");
						// query.setString("username", folder.getUsername());
						// query.executeUpdate();
						session.update(folder);

					}

				}
				for (String folderId : userObj.getPermissonFolderID().split(",")) {

					folderId = folderId.substring(0, folderId.indexOf("%%$%%"));
					int id = Integer.parseInt(folderId);

					Folder folder = (Folder) session.get(Folder.class, id);
					foldername += folder.getLabel() + ",";
					folder.setUsername(folder.getUsername() + "&&&***&&&" + userObj.getUsername() + "&&&***&&&");
					session.update(folder);

				}
			}
			if (foldername.length() > 0) {
				if (foldername.endsWith(",")) {
					foldername = foldername.substring(0, foldername.lastIndexOf(","));
				}
				user.setPermissonFolderID(userObj.getPermissonFolderID());
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

	public String saveDocument(InputStream is, String fileName, String documentProperties, Folder folder) {
		Session session = null;
		byte[] theString;
		Transaction trx = null;
		Document file = new Document();
		String filePath;
		try {
			theString = IOUtils.toByteArray(is);
			session = HibernateUtil.getSession();

			trx = session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM document d where d.name =:name and d.id =:folderId",
					Document.class);
			query.setString("name", fileName);
			query.setInteger("folderId", folder.getId());
			List list = query.list();
			// Object doc = session.get(Document.class, file.getFilename());
			fileName = URLEncoder.encode(fileName);
			if(list.size() >=0){
				fileName = generateFileName(fileName);
			}
//			if (list.size() == 0) {
				
				file.setFilename(fileName);
				file.setContent(theString);
				file.setFolderId(folder.getId());
				file.setParentId(folder.getParentid() + folder.getId());
				Folder docFolder = createDocFolder(folder, fileName, documentProperties);
				session.save(docFolder);// saving doc in folder table
				session.save(file);// save doc content in
									// other table
				trx.commit();
				session.close();
				filePath = folder.getId()+"/"+fileName;
				theString = null;
				file = null;
				IOUtils.closeQuietly(is);
				docFolder = null;
//			} else {
//				trx.rollback();
//				;
//				session.close();
//				return null;
//			}
			return filePath;
		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println("Exception occred while saveDocument: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		} finally {
			try {
				is.close();

				System.out.println("import content finally block called.");
			} catch (IOException e) {
			}

		}

		// TODO Auto-generated method stub

	}

	private String generateFileName(String fileName) {
		String ext =fileName.substring(fileName.lastIndexOf(".")); 
		fileName = fileName.replace(ext, gen()+ext);
		return fileName;
	}
	public int gen() {
	    Random r = new Random( System.currentTimeMillis() );
	    return r.nextInt(200000);
	}

	private Folder createDocFolder(Folder folder, String docName, String documentProperties) {
		Folder docFolder = new Folder();
		docFolder.setLabel(docName);
		docFolder.setParentid(folder.getParentid() + folder.getId());
		if (!folder.getUsername().contains(folder.getUsername() + "&&&***&&&"))
			docFolder.setUsername(folder.getUsername() + "&&&***&&&");
		docFolder.setProperties(documentProperties);
		return docFolder;
	}

	public Document retrieveDocument(int id, String filename) {

		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			// trx = session.beginTransaction();
			System.out.println("id-------" + id + " ---------" + filename);
			Query query = session.createNativeQuery("SELECT * FROM document d where d.name =:name and d.id =:folderId",
					Document.class);
			query.setString("name", filename);
			query.setInteger("folderId", id);
			List list = query.list();
			System.out.println("list-------" + list);
			Document doc = new Document();
			if (list != null && list.size() > 0)
				doc = (Document) list.get(0);
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
				query = session.createNativeQuery("SELECT * FROM folder f where f.isFolder=:isFolder order by name",
						Folder.class);
			else {
				query = session.createNativeQuery(
						"SELECT * FROM folder f where f.isFolder=:isFolder and UPPER(userName) LIKE :userName or f.id=1000000 order by name",
						Folder.class);
				query.setString("userName", "%" + username.toLowerCase() + "&&&***&&&%");
			}

			query.setBoolean("isFolder", true);
			lst = query.list();
			if (!isadmin) {
				for (Folder folderObj : lst) {
					if (folderObj.getId() != 1000000) {
						List<Folder> childList = retrieveSpecificFolders(folderObj.getParentid() + folderObj.getId(),
								username, isadmin, false);
						folderObj.setChildren(childList);
					}
				}
				List<Folder> tempList = new ArrayList<Folder>();
				for (Folder folder : lst) {
					getAllParents(lst, tempList, session, folder);

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

		if (folder.getId() != 1000000 && checkExists(lst, folder.getParentid()) == 0) {
			String parentid = folder.getParentid().substring(folder.getParentid().length() - 7);
			int pId = Integer.parseInt(parentid);
			Folder flder = (Folder) session.get(Folder.class, pId);
			if (!lst.contains(flder) && !tempList.contains(flder))
				tempList.add(flder);
			if (folder.getParentid().equalsIgnoreCase("1000000"))
				return;
			else
				getAllParents(lst, tempList, session, flder);

		}
	}

	private int checkExists(List<Folder> lst, String parentid) {
		parentid = parentid.substring(parentid.length() - 7);
		int pId = Integer.parseInt(parentid);
		for (Folder folder : lst) {

			if (folder.getId() == pId)
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
			String parentID = folder.getParentid().length() > 7
					? folder.getParentid().substring(folder.getParentid().length() - 7) : folder.getParentid();
			query.setString("id", parentID);
			lst = query.list();
			Folder parentFolder = (Folder) lst.get(0);
			folder.setUsername(parentFolder.getUsername() + "&&&***&&&" + folder.getUsername() + "&&&***&&&");

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
				int id = Integer.parseInt(folderId.substring(folderId.length() - 7));

				Folder folder = (Folder) session.get(Folder.class, id);
				if (folder != null
						&& folder.getUsername().toUpperCase().contains(username.toUpperCase() + "&&&***&&&")) {
					query = session.createNativeQuery("SELECT * FROM folder f where f.parentId = :parentId",
							Folder.class);
				} else {
					query = session.createNativeQuery(
							"SELECT * FROM folder f where f.parentId = :parentId  and  UPPER(userName) LIKE :userName",
							Folder.class);
					query.setString("userName", "%" + username.toUpperCase() + "&&&***&&&%");
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
						"SELECT * FROM folder f where f.parentId = :parentId  and UPPER(userName) =:userName and f.isFolder=true",
						Folder.class);
				query.setString("userName", username.toUpperCase());
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

			if (isadmin) {
				Folder folder = (Folder) session.get(Folder.class, Integer.parseInt(folderId));
				query = session.createNativeQuery("DELETE FROM folder WHERE id=:id OR parentId LIKE :parentId",
						Folder.class);
				query.setInteger("id", Integer.parseInt(folderId));
				query.setString("parentId", parentId + folderId + "%");
				result = query.executeUpdate();
				if (!folder.isFolder()) {
					query = session.createNativeQuery("DELETE FROM document WHERE name=:name and parentId  =:parentId",
							Document.class);
					query.setString("name", folder.getLabel());
					parentId = folder.getParentid().substring(0, folder.getParentid().length());
					query.setString("parentId", folder.getParentid());
					result = query.executeUpdate();
				} else if (folder.isFolder()) {
					query = session.createNativeQuery("DELETE FROM document WHERE id  =:id", Document.class);
					query.setInteger("id", Integer.parseInt(folderId));
					result = query.executeUpdate();
				}

			} else {
				query = session.createNativeQuery(
						"DELETE FROM folder WHERE id=:id OR parentId LIKE :parentId and UPPER(userName) =:username",
						Folder.class);
				query.setString("username", username.toUpperCase());
				query.setInteger("id", Integer.parseInt(folderId));
				query.setString("parentId", parentId + folderId + "%");
				result = query.executeUpdate();
				query = session.createNativeQuery(
						"DELETE FROM document WHERE id=:id OR parentId LIKE :parentId and UPPER(userName) =:username",
						Document.class);
				query.setString("username", username.toUpperCase());
				query.setInteger("id", Integer.parseInt(folderId));
				query.setString("parentId", parentId + folderId + "%");
				result = query.executeUpdate();
			}

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
				query = session.createNativeQuery("SELECT id FROM folder f where f.isFolder=true and f.userName LIKE :username");
				query.setString("username", "%&&&***&&&" + username + "&&&***&&&%");
				List<Integer> idList = query.list();
				// constructing add condition
				String andCondition = "";
				System.out.println("idList ---"+idList.size());
				for (int i = 0; i < idList.size(); i++) {
					andCondition += "parentId like '%" + idList.get(i) + "%'";
					if (i < idList.size() - 1)
						andCondition += " or ";
				}

				String queryString = "SELECT * FROM folder f where f.isFolder=false and (f.details LIKE :details or f.name LIKE :name)";
				if (andCondition != null && andCondition.length() > 0)
					queryString += "and (" + andCondition + " )";

				query = session.createNativeQuery(queryString, Folder.class);
				query.setString("details", "%" + searchString + "%");
				query.setString("name", "%" + searchString + "%");
			}

			// query.setBoolean("isFolder", true);
			lst = query.list();
			System.out.println("search result list size "+lst.size());
			// if (!isadmin) {
			// for (Folder folderObj : lst) {
			// if (folderObj.getId() != 1000000) {
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
			// Transaction t = session.beginTransaction();
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

	public Folder getFolder(String path) {
		path = path.replace("\\", "/");
		String pathAarray[] = path.split("/");
		String parentId = "NULL";
		Folder folder = null;
		for (String pathName : pathAarray) {
			folder = getFolderWithPath(pathName, parentId);
			if (parentId.equalsIgnoreCase("NULL"))
				parentId = folder.getId() + "";
			else
				parentId = parentId + folder.getId();
		}
		return folder;

	}

	private Folder getFolderWithPath(String path, String parentId) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			Query query = null;
			List<Folder> lst = null;
			query = session.createNativeQuery(
					"SELECT * FROM folder f where f.name =:path  and f.parentId LIKE :parentId", Folder.class);
			query.setString("path", path);
			query.setString("parentId", parentId);
			lst = query.list();

			Folder folder = lst.get(0);
			trx.commit();
			session.close();
			return folder;
		} catch (Exception exception) {
			System.out.println("Exception occred while getFolder: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		} finally {

		}

	}

	public User getUser(String username) {
		Session session = HibernateUtil.getSession();
		Transaction trx = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, username);

			trx.commit();
			session.close();
			return user;

		} catch (Exception exception) {
			System.out.println("Exception occred while login: " + exception.getMessage());
			if (trx != null)
				trx.rollback();
			if (session != null)
				session.close();
			return null;
		} finally {

		}

	}

}
