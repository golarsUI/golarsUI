package com.golars.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.golars.bean.Folder;
import com.golars.bean.User;
import com.golars.util.DBUtil;
import com.google.gson.Gson;

@Path("/users")
public class UsersService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveAllUsers() {

//		List<User> userList = new ArrayList<User>();
		List<User> userList = new DBUtil().getAllUsers();
//		for (int i = 0; i < 10; i++) {
//			User user = new User();
//			user.setUsername("username "+i);
//			if(i % 2 == 0)
//				user.setAdmin(true);
//			else
//				user.setAdmin(false);
//			userList.add(user);
//		}

		return Response.status(201).entity(new Gson().toJson(userList) ).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(User user) {
		new DBUtil().register(user);
//		List<KeyValue> docList = new ArrayList<KeyValue>();
//		for (int i = 0; i < 10; i++) {
//			KeyValue keyValue = new KeyValue();
//			keyValue.setKey(docId+" Key" + i);
//			keyValue.setValue(docId+" Value" + i);
//			docList.add(keyValue);
//		}

		return Response.status(201).entity(true).build();
	}


}