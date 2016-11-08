package app.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.components.UserComponent;
import app.entities.Build;
import app.entities.User;
import app.repositories.BuildRepository;

@Component
@Path("/")
public class UsersController extends AppController {

	@Autowired
	UserComponent userComp;
	
	@Autowired
	BuildRepository buildDao;
	
	@POST
	@Path("/register")			// similar to /admin/register and /seller/register
	public Response register(@FormParam("username") String username, @FormParam("email") String email, 
			@FormParam("password") String password, @FormParam("confirm_password") String confPassword, 
			@FormParam("is_seller") boolean isSeller, @FormParam("is_admin") boolean isAdmin,
			@Context HttpServletRequest req) {
		
		List<String> errors = userComp.validate(username, email, password, confPassword);
		if (errors.size() > 0) {
			return errorResponse(errors);
		}
		userComp.create(username, email, password, isSeller, isAdmin);
		userComp.setSession(req, username);
		return Response.ok().build();
	}
	
	@POST
	@Path("/login")			// similar to /admin/login and /seller/login
	public Response login(@FormParam("username_or_email") String usernameOrEmail, 
			@FormParam("password") String password, @Context HttpServletRequest req) {
		
		User user = userComp.findUser(usernameOrEmail);
		List<String> errors = userComp.authenticate(password, user);
		if (errors.size() > 0) {
			return errorResponse(errors);
		}
		userComp.setSession(req, user.getUsername());
		return Response.ok().build();
	}

	@GET
	@Path("/logout")			// similar to /admin/login and /seller/login
	public Response logout(@Context HttpServletRequest req) {
		userComp.setSession(req, null);
		return Response.ok().build();
	}
	
	@GET
	@Path("/u/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewUser(@PathParam("username") String username) {
		User user = userComp.findUser(username);
		if (user == null) {
			return Response.status(404).build();
		}
		
		List<Build> builds = buildDao.findByUser(user);
		for (Build b : builds) {
			b.setCreated(formatDate(b.getCreatedAt()));
		}
		user.setBuilds(builds);
		user.setCreated(formatDate(user.getCreatedAt()));
		return Response.ok().entity(user).build();
	}
	
	@GET
	@Path("/s/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewSeller(@PathParam("username") String username) {
		User user = userComp.findUser(username);
		if (user == null || !user.isSeller()) {
			return Response.status(404).build();
		}
		// TODO paginate parts (and maybe builds?)
		user.setBuilds(null);
		user.setCreated(formatDate(user.getCreatedAt()));
		return Response.ok().entity(user).build();
	}
	
	@GET
	@Path("/admin/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewAdmin(@PathParam("username") String username, @Context HttpServletRequest req) {
		User user = userComp.findUser(username);
		if (user == null || !user.isAdmin()) {
			return Response.status(404).build();
		}
		if (userComp.isUnauthorized(username, req)) {
			return Response.status(403).build();
		}
		user.setBuilds(null);
		// TODO paginate accounts
		user.setCreated(formatDate(user.getCreatedAt()));
		return Response.ok().entity(user).build();
	}
	
	public static class UserDto {
		private String username;
		private String email;
		private List<Build> builds;
		private String createdDate;
		public UserDto() {
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public List<Build> getBuilds() {
			return builds;
		}
		public void setBuilds(List<Build> builds) {
			this.builds = builds;
		}
		public String getCreatedDate() {
			return createdDate;
		}
		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}
	}
	
}