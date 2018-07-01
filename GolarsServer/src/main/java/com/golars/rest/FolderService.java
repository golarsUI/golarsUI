package com.golars.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.Folder;
import com.golars.bean.KeyValue;

@Path("/folders")
public class FolderService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveFolder(@QueryParam("folderId") String folderId,
			@QueryParam("documentsRequired") boolean docRequired) {

		List<Folder> folderList = new ArrayList<Folder>();
		for (int i = 0; i < 10; i++) {
			Folder folder = new Folder();
			folder.setId(i + "");
			folder.setLabel("name" + i);
			folder.setParentid("parentId" + i);
			folder.setExpandedIcon("fa fa-folder-open");
			folder.setCollapsedIcon("fa fa-folder");
			folder.setFolder(true);
			folderList.add(folder);
			if (!docRequired) {
				List<Folder> children = createDummyChildren();
				folder.setChildren(children);
			} else {
				List<Folder> children = createDummyFiles();
				folderList.addAll(children);
			}
		}

		return Response.status(201).entity(folderList).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createFolder(Folder folder) {
		String label = folder.getLabel();
		String parentFolderID = folder.getParentid();
		folder.setExpandedIcon("fa fa-folder-open");
		folder.setCollapsedIcon("fa fa-folder");
		System.out.println("folder is created with name--"+label+" in folder-->"+parentFolderID);
		return Response.status(201).entity(folder).build();

	}
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteeFolder(@QueryParam("documentId") String documentId) {

		

		return Response.status(201).entity(true).build();
	}

	@GET
	@Path("/documentdetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveDocumentDetailsr(@QueryParam("documentId") String docId) {

		List<KeyValue> docList = new ArrayList<KeyValue>();
		for (int i = 0; i < 10; i++) {
			KeyValue keyValue = new KeyValue();
			keyValue.setKey(docId+" Key" + i);
			keyValue.setValue(docId+" Value" + i);
			docList.add(keyValue);
		}

		return Response.status(201).entity(docList).build();
	}

	private List<Folder> createDummyFiles() {
		List<Folder> folderList = new ArrayList<Folder>();
		for (int i = 0; i < 5; i++) {
			Folder folder = new Folder();
			folder.setId(i + "");
			folder.setLabel("name" + i);
			folder.setParentid("parentId" + i);
			folder.setFolder(false);
			if (i % 2 == 0)
				folder.setIcon("fa fa-file-word-o");
			else
				folder.setIcon("fa fa-file-pdf-o");
			folderList.add(folder);
		}
		return folderList;
	}

	private List<Folder> createDummyChildren() {
		List<Folder> folderList = new ArrayList<Folder>();
		for (int i = 0; i < 2; i++) {
			Folder folder = new Folder();
			folder.setId(i + "");
			folder.setLabel("name" + i);
			folder.setParentid("parentId" + i);
			folder.setExpandedIcon("fa fa-folder-open");
			folder.setCollapsedIcon("fa fa-folder");
			folderList.add(folder);
			folder.setFolder(true);
		}
		return folderList;
	}
}