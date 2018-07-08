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
import com.golars.bean.UserSettings;
import com.golars.util.DBUtil;
import com.golars.util.GolarsUtil;
import com.google.gson.Gson;

@Path("/folders")
public class FolderService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveFolder(@QueryParam("folderId") String folderId, @QueryParam("parentId") String parentId,
			@QueryParam("documentsRequired") boolean docRequired, @QueryParam("username") String username,
			@QueryParam("isAdmin") boolean isadmin) {
		List<Folder> folderList = new ArrayList<Folder>();
		if (folderId.equals("-1"))
			folderList = new DBUtil().retrieveAllFolders(username, isadmin);
		else {
			String parentFOlderId = parentId.equalsIgnoreCase("null") ? folderId : parentId + folderId;
			if(docRequired)
			folderList = new DBUtil().retrieveSpecificFolders(parentFOlderId, username, isadmin);
			else
				folderList = new DBUtil().retrieveSpecificFolders(parentFOlderId, username, isadmin,docRequired);
				
		}
		if (folderId.equals("-1")) {
			Folder folder = GolarsUtil.getChildren(GolarsUtil.getCurrentNode(1000, folderList), folderList);
			folderList.clear();
			folderList.add(folder);
		}

		return Response.status(200).entity(folderList).build();
	}

	@GET
	@Path("/preferences")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveFolderTablePreferences(@QueryParam("isAdmin") boolean isadmin) {
		String preferenceName = getPreferenceString(isadmin);
		List<UserSettings> preferences = new DBUtil().retrieveUserPreferences();
		return Response.status(200).entity(preferences).build();
	}
	
	@POST
	@Path("/preferences")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response saveFolderTablePreferences(String userSettings) {
		UserSettings[] settingArray = new Gson().fromJson(userSettings, UserSettings[].class);
		new DBUtil().updatePreferences(settingArray);
		
//		String preferenceName = getPreferenceString(isadmin);
//		List<UserSettings> preferences = new DBUtil().retrieveUserPreferences();
//		new DBUtil().retrieveUserPreferences();
//		List<UserSettings> preferences = new DBUtil().retrieveUserPreferences();
		List<UserSettings> preferences = new DBUtil().retrieveUserPreferences();
		return Response.status(200).entity(preferences).build();
	}

	private String getPreferenceString(boolean isadmin) {
		if (isadmin)
			return "adminTableColumns";
		else
			return "nonAdminTableColumns";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createFolder(Folder folder) {
		String label = folder.getLabel();
		String parentFolderID = folder.getParentid();
		Folder returnedfolder = new DBUtil().createFolder(folder);
		if (returnedfolder == null)
			return Response.status(200).entity(returnedfolder).build();
		System.out.println("folder is created with name--" + label + " in folder-->" + parentFolderID);
		return Response.status(200).entity(returnedfolder).build();

	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteeFolder(@QueryParam("folderId") String folderId, @QueryParam("parentId") String parentId,@QueryParam("username") String username,
			@QueryParam("isAdmin") boolean isadmin) {

		int result = new DBUtil().deleteFolder(folderId, parentId,username,isadmin);

		return Response.status(200).entity(result).build();
	}

	// @GET
	// @Path("/documentdetails")
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response retrieveDocumentDetailsr(@QueryParam("documentName")
	// String documentName) {
	//
	// String properties =new DBUtil().fetchDocDetails(documentName);
	//
	// return Response.status(201).entity(properties).build();
	// }

}