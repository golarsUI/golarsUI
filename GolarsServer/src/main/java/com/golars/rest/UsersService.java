package com.golars.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.ChangePassword;
import com.golars.bean.User;
import com.golars.util.DBUtil;
import com.google.gson.Gson;

@Path("/users")
public class UsersService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveAllUsers() {

		List<User> userList = new DBUtil().getAllUsers();
		return Response.status(201).entity(new Gson().toJson(userList) ).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(User user) {
		boolean result = new DBUtil().register(user);
		return Response.status(201).entity(result).build();
	}
	@POST
	@Path("/changepassword")
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response changePassword(ChangePassword changePasswordObj) {
		boolean result = new DBUtil().changePassword(changePasswordObj);
		
		return Response.status(201).entity(result).build();
	}

}