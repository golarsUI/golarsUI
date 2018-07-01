package com.golars.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.User;
import com.golars.bean.UserResponse;
import com.golars.util.TokenGenerator;

@Path("/login")
public class LoginService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response login(User user) {
		boolean isLoginSuccess = false;
		String username = user.getUsername();
		String password = user.getPassword();
		UserResponse response = new UserResponse();
		if(username == null || password == null){
			response.setLoginsuccess(isLoginSuccess);
			return Response.status(403).entity(response).build();
		}
			// need to return failure object
		//need to replace below code with db call
		if (username.equals("admin") && password.contains("admin")) {
			isLoginSuccess = true;
		}
		
		if(isLoginSuccess){
		response.setLoginsuccess(isLoginSuccess);
		response.setUsername(username);
		response.setToken(new TokenGenerator().generateToken(username));
		}

		return Response.status(200).entity(response).build();

	}

}