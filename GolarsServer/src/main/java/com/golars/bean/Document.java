package com.golars.bean;

public class Document {

	private String filename;
	private byte[] content;
	private String properties;
	private String parentId;
	private String folderId;
	public String getFilename() {
		return filename;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
