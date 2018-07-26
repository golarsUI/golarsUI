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
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.golars.bean.Folder;
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
					try{
					Row row = rowIterator.next();
					//For each row, iterate through all the columns
					JsonObject bean = new JsonObject();
					Cell cell = row.getCell(0);
					java.util.Date dateValue = null;
					if(cell != null)
						dateValue = cell.getDateCellValue();
					if(dateValue!=null)
					bean.addProperty("docUpdateDate",new Date(dateValue.getTime())+"");
					cell = row.getCell(1);
					if(cell != null)
					bean.addProperty("fecilityName",getCellValue(cell));
					cell = row.getCell(2);
					if(cell != null)
					dateValue = cell.getDateCellValue();
					if(dateValue!=null)
					bean.addProperty("docDate",new Date(dateValue.getTime())+"");
					cell = row.getCell(3);
					if(cell != null)
					bean.addProperty("fid",getCellValue(cell)+"");
					cell = row.getCell(4);
					if(cell != null) 
					bean.addProperty("stateProgram",getCellValue(cell));
					cell = row.getCell(5);
					if(cell != null)
					bean.addProperty("docTypes",getCellValue(cell));
					cell = row.getCell(6);
					if(cell != null)
					bean.addProperty("scopeOfWork",getCellValue(cell));
					cell = row.getCell(7);
					String path = null;
					if(cell != null)
						path = getCellValue(cell);
					cell = row.getCell(8);
					String remoteURL =null;
					if(cell != null)
						remoteURL = getCellValue(cell);
					if(remoteURL == null) break;
					String url = null;
					try
					{	
						url = remoteURL.substring(remoteURL.indexOf("fileName=")+"fileName=".length());
					}catch(Exception e){
						url = gen()+"";
					}
					bean.addProperty("url",url);
				   URL xyz = new URL( getCellValue(cell));
				       URLConnection xyzcon = xyz.openConnection();
				       bean.addProperty("username",userName);
				       Folder folder =  new DBUtil().getFolder(path);
				       new DBUtil().saveDocument(xyzcon.getInputStream(), url, new Gson().toJson(bean),folder);
				       insertCount++;
				       System.out.println(insertCount+" Files(s) imported of "+totalcount);
				       System.out.println("File imported is "+url+" into "+path+" ");
				       
				       
				       
				       writeEntriesIntoFile(remoteURL,"http://golars360.com/golars/rest/import/"+folder.getId()+"/"+url);;
					}catch(Exception e){
						System.out.println("Skip file import "+e.getMessage());
					}
				      
				}

				file.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			 System.out.println("Imported documents succesfully. "+totalcount+" documents Imported");
			 User user = new DBUtil().getUser(userName);
			 new MailUtil().bulkImportEmail(user,totalcount);
	}

		private String getCellValue(Cell cell) {
			if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
            {
                return cell.getStringCellValue()+" ";
            }
            else if(cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN)
            {
                return cell.getBooleanCellValue()+" ";
            }else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
            {
                return cell.getNumericCellValue()+" ";
            }else if(cell.getCellType() == XSSFCell.CELL_TYPE_BLANK)
            {
                return " ";
            }else if(cell.getCellType() == XSSFCell.CELL_TYPE_ERROR)
            {
                return " ";
            }else if(cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA)
            {
                return " ";
            }
			return " ";
		}

		private void writeEntriesIntoFile(String remoteURL, String localURL) {
			 String excelFilePath = "c:\\golars\\bulkimport\\urlmapping.xlsx";
	         
		        try {
		            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		            Workbook workbook = WorkbookFactory.create(inputStream);
		 
		            Sheet sheet = workbook.getSheetAt(0);
		 
		            Object[][] bookData = {
		                    { remoteURL, localURL},
		            };
		 
		            int rowCount = sheet.getLastRowNum();
		 
		            for (Object[] aBook : bookData) {
		                Row row = sheet.createRow(++rowCount);
		 
		                int columnCount = -1;
		                 
		                 
		                for (Object field : aBook) {
		                	Cell cell= row.createCell(++columnCount);
		                    if (field instanceof String) {
		                        cell.setCellValue((String) field);
		                    } else if (field instanceof Integer) {
		                        cell.setCellValue((Integer) field);
		                    }
		                }
		 
		            }
		 
		            inputStream.close();
		 
		            FileOutputStream outputStream = new FileOutputStream("c:\\golars\\bulkimport\\urlmapping.xlsx");
		            workbook.write(outputStream);
		            outputStream.close();
		             
		        } catch (IOException | EncryptedDocumentException
		                | InvalidFormatException ex) {
		          System.out.println("unable to write to excel path c:\\golars\\bulkimport\\urlmapping.xlsx "+ex.getMessage());
		        }
		    }
			
		public int gen() {
		    Random r = new Random( System.currentTimeMillis() );
		    return 100000 + r.nextInt(200000);
		}
}