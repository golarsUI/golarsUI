package com.golars.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.User;
import com.golars.mail.MailUtil;
import com.golars.util.DBUtil;

@Path("/forgotpassword")
public class ForgotPasswordService {

	

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(String inputString) {
//		String email = new Gson().fromJson(emailAddress, JsonObject.class).get("email").toString();
		String inputArray[] = inputString.split("&&@@#@");
		boolean result = false;
		User userobj = new DBUtil().checkUserPresent(inputArray[0]);
	
		if(userobj!=null){
			new MailUtil().sendforgotPasswordEmail(userobj.getEmailAddress(),userobj.getUsername(),inputArray[1]);
			result = true;
		}
		return Response.status(201).entity(result).build();
	}
	
	

}