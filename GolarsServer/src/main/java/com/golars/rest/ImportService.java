package com.golars.rest;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.Document;
import com.golars.bean.Folder;
import com.golars.util.DBUtil;
import com.google.gson.Gson;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/import")
public class ImportService {

	@POST

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@FormDataParam("fileUpload") FormDataBodyPart body,@FormDataParam("docProperties") String documentProperties,@FormDataParam("folderProperties") String folderProperties) {
		boolean result = false;
		
		for (BodyPart part : body.getParent().getBodyParts()) {

			InputStream is = part.getEntityAs(InputStream.class);
			ContentDisposition meta = part.getContentDisposition();
			if(meta.getFileName()!=null){
				Folder folder = new Gson().fromJson(folderProperties, Folder.class);
				result  =  new DBUtil().saveDocument(is, meta.getFileName(),documentProperties,folder);
			}
		}
		return Response.status(200).entity(result).build();
	}
	
	@Path("{id}")
	@GET
	public Response getPDF(@PathParam("id") String filename) throws Exception {
		Document doc =  new DBUtil().retrieveDocument(filename);
	    return Response.ok(doc.getContent(), MediaType.APPLICATION_OCTET_STREAM) //TODO: set content-type of your file
	            .header("content-disposition", "attachment; filename = "+doc.getFilename())
	            .build();
	}

}