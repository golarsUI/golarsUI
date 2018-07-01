package com.golars.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.KeyValue;
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
	public Response uploadFile(@FormDataParam("fileUpload") FormDataBodyPart body,@FormDataParam("docProperties") String documentProperties) {
		
		
		for (BodyPart part : body.getParent().getBodyParts()) {

			InputStream is = part.getEntityAs(InputStream.class);
			ContentDisposition meta = part.getContentDisposition();
			if(meta.getFileName()!=null){
			 String uploadedFileLocation = "c:\\test\\test\\"+ meta.getFileName();
			    writeToFile(is, uploadedFileLocation);
			}
		}
		KeyValue[] keyValues = new Gson().fromJson(documentProperties, KeyValue[].class);
		return Response.status(200).entity(true).build();
	}
	
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
	        String uploadedFileLocation) {

	    try {
	        OutputStream out = new FileOutputStream(new File(
	                uploadedFileLocation));
	        int read = 0;
	        byte[] bytes = new byte[1024];

	        out = new FileOutputStream(new File(uploadedFileLocation));
	        while ((read = uploadedInputStream.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        out.flush();
	        out.close();
	    } catch (IOException e) {

	        e.printStackTrace();
	    }

	   }


}