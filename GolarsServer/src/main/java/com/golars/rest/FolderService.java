package com.golars.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
	static{
		Timer t = new Timer( );
		t.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		    	System.out.println("Db call-------------");
		    	DBUtil.getInstance().retrieveUserPreferences();

		    }
		}, 10000,3600000);
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveFolder(@QueryParam("folderId") String folderId, @QueryParam("parentId") String parentId,
			@QueryParam("documentsRequired") boolean docRequired, @QueryParam("username") String username,
			@QueryParam("isAdmin") boolean isadmin) {
		List<Folder> folderList = new ArrayList<Folder>();
		if (folderId.equals("-1"))
			folderList = DBUtil.getInstance().retrieveAllFolders(username, isadmin);
		else {
			String parentFOlderId = parentId.equalsIgnoreCase("null") ? folderId : parentId + folderId;
			if(docRequired)
			folderList = DBUtil.getInstance().retrieveSpecificFolders(parentFOlderId, username, isadmin);
			else
				folderList = DBUtil.getInstance().retrieveSpecificFolders(parentFOlderId, username, isadmin,docRequired);
				
		}
		if (folderId.equals("-1")) {
			Folder folder = GolarsUtil.getChildren(GolarsUtil.getCurrentNode(1000000, folderList), folderList);
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
		List<UserSettings> preferences = DBUtil.getInstance().retrieveUserPreferences();
		return Response.status(200).entity(preferences).build();
	}
	
	@POST
	@Path("/preferences")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response saveFolderTablePreferences(String userSettings) {
		UserSettings[] settingArray = new Gson().fromJson(userSettings, UserSettings[].class);
		DBUtil.getInstance().updatePreferences(settingArray);
		
//		String preferenceName = getPreferenceString(isadmin);
//		List<UserSettings> preferences = DBUtil.getInstance().retrieveUserPreferences();
//		DBUtil.getInstance().retrieveUserPreferences();
//		List<UserSettings> preferences = DBUtil.getInstance().retrieveUserPreferences();
		List<UserSettings> preferences = DBUtil.getInstance().retrieveUserPreferences();
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
		Folder returnedfolder = DBUtil.getInstance().createFolder(folder);
		if (returnedfolder == null)
			return Response.status(200).entity(returnedfolder).build();
		System.out.println("folder is created with name--" + label + " in folder-->" + parentFolderID);
		return Response.status(200).entity(returnedfolder).build();

	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteeFolder(@QueryParam("folderId") String folderId, @QueryParam("parentId") String parentId,@QueryParam("username") String username,
			@QueryParam("isAdmin") boolean isadmin) {

		int result = DBUtil.getInstance().deleteFolder(folderId, parentId,username,isadmin);

		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchSearchResults(@QueryParam("searchString") String searchString, @QueryParam("username") String username,
			@QueryParam("isAdmin") boolean isadmin) {
		List<Folder> folderList = new ArrayList<Folder>();
			folderList = DBUtil.getInstance().retrieveSearchResults(searchString,username, isadmin);

		return Response.status(200).entity(folderList).build();
	}

}