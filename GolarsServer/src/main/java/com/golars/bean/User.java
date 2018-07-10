package com.golars.bean;

public class User {

	private String firstName;
	private String lastName;
	private String emailAdress;
	private String username;
	private String password;
	private boolean admin;
	private boolean newlyCreated;
	private String permissonFolderID;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAdress() {
		return emailAdress;
	}

	public void setEmailAdress(String emailAdress) {
		this.emailAdress = emailAdress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public void setNewlyCreated(boolean newlyCreated) {
		this.newlyCreated = newlyCreated;
	}

	public boolean isNewlyCreated() {
		return newlyCreated;
	}

	public void setPermissonFolderID(String permissonFolderID) {
		this.permissonFolderID = permissonFolderID;
	}

	public String getPermissonFolderID() {
		return permissonFolderID;
	}
}
