package com.golars.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

public class Folder {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String label;
	private String parentid;
	private boolean folder;
	private List<Folder> children = new ArrayList<Folder>();
	private String username;

//	public Folder(String id, String parentid, String label) {
//		this.id = id;
//		this.parentid = parentid;
//		this.label = label;
//	}

	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public void setChildren(List<Folder> children) {
		this.children = children;
	}

	public List<Folder> getChildren() {
		return children;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public boolean isFolder() {
		return folder;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
}
