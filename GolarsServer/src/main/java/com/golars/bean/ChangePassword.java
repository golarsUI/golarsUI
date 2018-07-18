package com.golars.bean;

public class ChangePassword{
	

	private String username;
	private String password;
	private String updatedPassword;
	private boolean reset = false;
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
	public String getUpdatedPassword() {
		return updatedPassword;
	}
	public void setUpdatedPassword(String updatedPassword) {
		this.updatedPassword = updatedPassword;
	}
	public void setReset(boolean reset) {
		this.reset = reset;
	}
	public boolean isReset() {
		return reset;
	}
	
	}
