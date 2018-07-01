package com.golars.bean;

public class UserResponse{
	

	private String username;
	private String token;
	private boolean loginsuccess;
	private boolean admin;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
