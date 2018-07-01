package com.golars.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.golars.bean.User;

public class DBUtil {
	public User login(String username, String password) {
		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			User user = (User) session.get(User.class, username);
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
		}
		return null;
	}

	public boolean register(User userObj) {
		Session session = HibernateUtil.getSession();
		Transaction tx1 = session.beginTransaction();
		try {
			String abc = (String) session.save(userObj);
			return true;
		} catch (Exception exception) {
			System.out.println("Exception occred while reading user data: " + exception.getMessage());
			tx1.rollback();
			return false;
		} finally {
			tx1.commit();
		}
	}
}
