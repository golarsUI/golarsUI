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
import com.golars.util.DBUtil;
import com.golars.util.GolarsUtil;

@Path("/folders")
public class FolderService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveFolder(@QueryParam("folderId") String folderId,@QueryParam("parentId") String parentId,
			@QueryParam("documentsRequired") boolean docRequired) {
		List<Folder> folderList = new ArrayList<Folder>();
		if(folderId.equals("-1"))
			folderList= new DBUtil().retrieveAllFolders();
		else
		{
			String parentFOlderId = parentId.equalsIgnoreCase("null")?folderId:parentId+folderId;
			folderList= new DBUtil().retrieveSpecificFolders(parentFOlderId);
		}
		if(folderId.equals("-1"))
		{
			Folder folder = GolarsUtil.getChildren(GolarsUtil.getCurrentNode(1000, folderList), folderList);
			folderList.clear();
			folderList.add(folder);
		}

		return Response.status(201).entity(folderList).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createFolder(Folder folder) {
		String label = folder.getLabel();
		String parentFolderID = folder.getParentid();
		Folder returnedfolder = new DBUtil().createFolder(folder);
		System.out.println("folder is created with name--"+label+" in folder-->"+parentFolderID);
		return Response.status(201).entity(returnedfolder).build();

	}
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteeFolder(@QueryParam("folderId") String folderId,@QueryParam("parentId")String parentId) {

		int result =  new DBUtil().deleteFolder(folderId,parentId);

		return Response.status(201).entity(result).build();
	}

	@GET
	@Path("/documentdetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveDocumentDetailsr(@QueryParam("documentName") String documentName) {

		String properties =new DBUtil().fetchDocDetails(documentName);

		return Response.status(201).entity(properties).build();
	}

}