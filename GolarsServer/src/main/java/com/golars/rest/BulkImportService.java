package com.golars.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.golars.bean.User;
import com.golars.mail.MailUtil;
import com.golars.util.DBUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/bulkimport")
public class BulkImportService {
	String fileName = null;
	  @POST
	    @Consumes(MediaType.MULTIPART_FORM_DATA)
	    public Response uploadExcelFile(@FormDataParam("fileUpload") FormDataBodyPart body,@FormDataParam("userName") final String userName) {
		  for (BodyPart part : body.getParent().getBodyParts()) {

				InputStream is = part.getEntityAs(InputStream.class);
				ContentDisposition meta = part.getContentDisposition();
				if (meta.getFileName() != null) {
					try {
						fileName =meta.getFileName();
						byte[] theString = IOUtils.toByteArray(is);
						FileOutputStream out = new FileOutputStream(new File(fileName));
						out.write(theString);
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				}
		  FileInputStream file;
		try {
			file = new FileInputStream(new File(fileName));
		

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			int totalURLCount = 0;
			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			int totalcount = sheet.getLastRowNum() - sheet.getFirstRowNum();
			
			for (int i = 0; i < sheet.getLastRowNum()-sheet.getFirstRowNum(); i++) {
				Cell cell = 	sheet.getRow(i).getCell(sheet.getRow(0).getLastCellNum()-1);
				if(!cell.getStringCellValue().equalsIgnoreCase(""))
					totalURLCount++;
			}
			
			if(totalcount != totalURLCount)
				 return Response.status(200).entity(10).build();
		} catch (Exception e) {
			 return Response.status(200).entity(-1).build();
		}
		  Thread bulkThread = new Thread(){
			 public void run() {
				 startImportingDocuments(userName,fileName);
			 }
		 };
		 bulkThread.start();
	  
		  
		  
       return Response.status(200).entity(0).build();
	    }
	 
	    public void startImportingDocuments(String userName, String fileName) {
	    	int totalcount = 0;
	    	int insertCount = 0;
	    	try
			{
				FileInputStream file = new FileInputStream(new File(fileName));

				//Create Workbook instance holding reference to .xlsx file
				XSSFWorkbook workbook = new XSSFWorkbook(file);

				//Get first/desired sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);
				totalcount = sheet.getLastRowNum() - sheet.getFirstRowNum();
				
				
				//Iterate through each rows one by one
					
				Iterator<Row> rowIterator = sheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) 
				{
					Row row = rowIterator.next();
					//For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();
					JsonObject bean = new JsonObject();
					Cell cell = cellIterator.next();
					bean.addProperty("docUpdateDate",new Date(cell.getDateCellValue().getTime())+"");
					cell = cellIterator.next();
					bean.addProperty("fecilityName",cell.getStringCellValue());
					cell = cellIterator.next();
					bean.addProperty("docDate",new Date(cell.getDateCellValue().getTime())+"");
					cell = cellIterator.next();
					bean.addProperty("fid",cell.getNumericCellValue()+"");
					cell = cellIterator.next();
					bean.addProperty("stateProgram",cell.getStringCellValue());
					cell = cellIterator.next();
					bean.addProperty("docTypes",cell.getStringCellValue());
					cell = cellIterator.next();
					bean.addProperty("scopeOfWork",cell.getStringCellValue());
					cell = cellIterator.next();
					String path = cell.getStringCellValue();
					cell = cellIterator.next();
					String url = cell.getStringCellValue().substring(cell.getStringCellValue().indexOf("fileName=")+"fileName=".length());
					bean.addProperty("url",url);
				   URL xyz = new URL(cell.getStringCellValue());
				       URLConnection xyzcon = xyz.openConnection();
				       bean.addProperty("username",userName);
				       new DBUtil().saveDocument(xyzcon.getInputStream(), url, new Gson().toJson(bean), new DBUtil().getFolder(path));
				       insertCount++;
				       System.out.println(insertCount+" Files(s) imported of "+totalcount);
				       System.out.println("File imported is "+url+" into "+path+" ");
				      
				}

				file.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		if(totalcount == insertCount){
			 System.out.println("Imported documents succesfully. "+totalcount+" documents Imported");
			 User user = new DBUtil().getUser(userName);
			 new MailUtil().bulkImportEmail(user,totalcount);
		}
	}

	
}