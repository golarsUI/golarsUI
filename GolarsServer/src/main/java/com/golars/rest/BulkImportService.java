package com.golars.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.golars.util.DBUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/bulkimport")
public class BulkImportService {
	  @POST
	    @Consumes(MediaType.MULTIPART_FORM_DATA)
	    public Response uploadExcelFile(@FormDataParam("fileUpload") FormDataBodyPart body) {
	 
		  for (BodyPart part : body.getParent().getBodyParts()) {

				InputStream is = part.getEntityAs(InputStream.class);
				ContentDisposition meta = part.getContentDisposition();
				if (meta.getFileName() != null) {
					try {
						byte[] theString = IOUtils.toByteArray(is);
						FileOutputStream out = new FileOutputStream(new File("c:\\test\\test.xlsx"));
						out.write(theString);
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				}
		  Thread bulkThread = new Thread(){
			 public void run() {
				 startImportingDocuments();
			 }
		 };
		 bulkThread.start();
	  
		  
		  
       return Response.ok("Bulk import in progress. Please check the server logs for update").build();
	    }
	 
	    public void startImportingDocuments() {
	    	try
			{
				FileInputStream file = new FileInputStream(new File("c:\\test\\test.xlsx"));

				//Create Workbook instance holding reference to .xlsx file
				XSSFWorkbook workbook = new XSSFWorkbook(file);

				//Get first/desired sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);
				int totalcount = sheet.getLastRowNum() - sheet.getFirstRowNum();
				int totalURLCount = 0;
				for (int i = 0; i < sheet.getLastRowNum()-sheet.getFirstRowNum(); i++) {
					Cell cell = 	sheet.getRow(i).getCell(sheet.getRow(0).getLastCellNum()-1);
					if(!cell.getStringCellValue().equalsIgnoreCase(""))
						totalURLCount++;
				}
				
				if(totalcount != totalURLCount)
				System.out.println("url fileds are less");
				else
				{
				//Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) 
				{
					Row row = rowIterator.next();
					//For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();
					SimpleDateFormat format = new SimpleDateFormat("mm dd yyyy");
					JsonObject bean = new JsonObject();
					Cell cell = cellIterator.next();
					bean.addProperty("docUpdateDate",format.parse(cell.getDateCellValue()+"")+"");
					cell = cellIterator.next();
					bean.addProperty("fecilityName",cell.getStringCellValue());
					cell = cellIterator.next();
					bean.addProperty("docDate",format.parse(cell.getDateCellValue()+"")+"");
					cell = cellIterator.next();
					bean.addProperty("fid",cell.getNumericCellValue()+"");
					cell = cellIterator.next();
					bean.addProperty("stateProgram",cell.getStringCellValue());
					cell = cellIterator.next();
					bean.addProperty("docTypes",cell.getStringCellValue());
					cell = cellIterator.next();
					bean.addProperty("scopeOfWork",cell.getStringCellValue());
					cell = cellIterator.next();
					String url = cell.getStringCellValue().substring(cell.getStringCellValue().indexOf("fileName=")+"fileName=".length());
					bean.addProperty("url",url);
				   URL xyz = new URL(cell.getStringCellValue());
				       URLConnection xyzcon = xyz.openConnection();
//				       BufferedReader in = new BufferedReader(new InputStreamReader());
				       new DBUtil().saveDocument(xyzcon.getInputStream(), url, new Gson().toJson(bean), new DBUtil().getFolder(1210));
//				       FileOutputStream out = new FileOutputStream("c:\\test\\"+url);
//				        String inputLine;
//				       byte[] abc =  IOUtils.toByteArray(in);
////				       while ((inputLine = in.));
//				       out.write(abc);
//				       out.close();
//				       in.close();
				}
				}
				file.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		
	}

	
}