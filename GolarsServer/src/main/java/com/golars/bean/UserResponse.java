package com.golars.bean;

public class UserResponse{
	

	private String fullName;
	private String token;
	private boolean loginsuccess;
	private boolean admin;

	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean isLoginsuccess() {
		return loginsuccess;
	}
	public void setLoginsuccess(boolean loginsuccess) {
		this.loginsuccess = loginsuccess;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean isAdmin() {
		return admin;
	}

	

}
