package com.golars.bean;

import java.io.Serializable;

public class Document implements Serializable{

	private String filename;
	private byte[] content;

	private String parentId;
	private int folderId;
	public String getFilename() {
		return filename;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	public int getFolderId() {
		return folderId;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
