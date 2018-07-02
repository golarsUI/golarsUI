package com.golars.bean;

import java.time.temporal.IsoFields;
import java.util.List;

public class Folder {
	private String id = "";
	private String label = "";
	private String parentid;
	private List<Folder> children;
	private String icon;
	private boolean folder;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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


	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}
}
