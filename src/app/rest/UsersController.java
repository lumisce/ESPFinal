package app.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import app.components.UserComponent;
import app.entities.Build;
import app.entities.Part;
import app.entities.Type;
import app.entities.User;
import app.repositories.BuildRepository;
import app.repositories.PartRepository;
import app.repositories.TypeRepository;
import app.repositories.UserRepository;

@Component
@Path("/")
public class UsersController extends AppController {

	@Autowired
	UserComponent userComp;
	
	@Autowired
	PartRepository partDAO;
	
	@Autowired
	UserRepository userDAO;
	
	@Autowired
	TypeRepository typeDAO;
	
	@Autowired
	BuildRepository buildDAO;
	
	@POST
	@Path("/register")			// similar to /admin/register and /seller/register
	public Response register(@FormParam("username") String username, @FormParam("email") String email, 
			@FormParam("password") String password, @FormParam("phone") String phone, @FormParam("confirm_password") String confPassword, 
			@FormParam("is_seller") boolean isSeller, @FormParam("is_admin") boolean isAdmin,
			@Context HttpServletRequest req) {
		
		List<String> errors = userComp.validate(username, email, password, confPassword);
		if (errors.size() > 0) {
			return errorResponse(errors);
		}
		isSeller = true;
		isAdmin = false;
		userComp.create(username, email, phone, password, isSeller, isAdmin);
//		userComp.setSession(req, username);
		return Response.ok().build();
	}
	
	@POST
	@Path("/login")			// similar to /admin/login and /seller/login
	public Response login(@FormParam("username_or_email") String usernameOrEmail, 
			@FormParam("password") String password) {
		
		User user = userComp.find(usernameOrEmail);
		userComp.authenticate(password, user);
//		userComp.setSession(req, user.getUsername());
		return Response.ok(user.getUsername()).build();
	}

//	@GET
//	@Path("/logout")			// similar to /admin/login and /seller/login
//	public Response logout(@Context HttpServletRequest req) {
////		userComp.setSession(req, null);
//		return Response.ok().build();
//	}
	
	@GET
	@Path("/users/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewUser(@PathParam("username") String username) {
		User user = userComp.find(username);
		
		List<Build> builds = userComp.findBuilds(user);
		for (Build b : builds) {
			b.setCreated(formatDate(b.getCreatedAt()));
		}
		user.setBuilds(builds);
		user.setCreated(formatDate(user.getCreatedAt()));
		return Response.ok().entity(user).build();
	}
	
	@GET
	@Path("/sellers/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewSeller(@PathParam("username") String username) {
		User user = userComp.find(username);
		userComp.checkSeller(user);
		// TODO paginate parts (and maybe builds?)
		user.setBuilds(null);
		user.setCreated(formatDate(user.getCreatedAt()));
		return Response.ok().entity(user).build();
	}
	
	@GET
	@Path("/admin/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewAdmin(@PathParam("username") String username, @Context HttpServletRequest req) {
		User user = userComp.find(username);
		userComp.checkAdmin(user);
//		userComp.checkAuthorized(username, req);
		
		user.setBuilds(null);
		// TODO paginate accounts
		user.setCreated(formatDate(user.getCreatedAt()));
		return Response.ok().entity(user).build();
	}
	
	@GET
	@Path("/builds")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Build> viewBuilds(@QueryParam("page") int page) {
		Pageable pg = new PageRequest(page, 25);
		List<Build> builds =  buildDAO.findAll(pg).getContent();
		for (Build b : builds) {
			b.setCreated(formatDate(b.getCreatedAt()));
			b.setUsername(b.getUser().getUsername());
		}
		return builds;
	}
	
	@GET
	@Path("/parts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Part> viewParts(@QueryParam("q") String query, @QueryParam("type") Long type, @QueryParam("page") int page, @Context HttpServletRequest req) {
		Pageable pg = new PageRequest(page, 25);
		if (query.isEmpty() && type == 0) {
			return partDAO.findAll(pg).getContent();
		} else if (query.isEmpty()) {
			Type t = typeDAO.findOne(type);
			return partDAO.findByType(t, pg).getContent();
		} else if (type == 0) {
			return partDAO.findByNameLike("%"+query+"%", pg).getContent();
		} 
		
		Type t = typeDAO.findOne(type);
		return partDAO.findByTypeAndNameLike(t, "%"+query+"%", pg).getContent();
	}
	
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Type> viewTypes(@Context HttpServletRequest req) {
		return typeDAO.findAll();
	}
	
	@POST
	@Path("/sellers/{seller}/parts/new")
	public Response newPart(@PathParam("seller") String username, @FormParam("name") String name, 
			@FormParam("price") Double price, @FormParam("description") String desc, 
			@FormParam("img_path") String imagePath, @FormParam("type_id") Long type) {
		
		User user = userComp.find(username);
		userComp.checkSeller(user);
//		userComp.checkAuthorized(username, req);
		
		Part newPart = new Part();
		newPart.setName(name);
		newPart.setPrice(price);
		newPart.setDescription(desc);
		newPart.setImagePath(imagePath);
		Type t = typeDAO.findOne(type);
		newPart.setType(t);
		newPart.setSeller(user);
		partDAO.save(newPart);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/sellers/{seller}/parts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Part> viewMyParts(@PathParam("seller") String username) {
		User user = userComp.find(username);
		userComp.checkSeller(user);
		
		List<Part> myParts = partDAO.findBySeller(user);
		return myParts;
	}
	
	@POST
	@Path("/sellers/{seller}/parts/{part}/edit")
	public Response editPart(@PathParam("seller") String username, @PathParam("part") Long id, 
			@FormParam("price") Double price, @FormParam("description") String desc, 
			@FormParam("img_path") String imagePath, @FormParam("type_id") Long type, 
			@Context HttpServletRequest req ) {
		
		User user = userComp.find(username);
		userComp.checkSeller(user);
//		userComp.checkAuthorized(username, req);
		
		Part toEdit = partDAO.findOne(id);
		
		if(price != null ) toEdit.setPrice(price);
		if(desc != null ) toEdit.setDescription(desc);
		if(imagePath != null ) toEdit.setImagePath(imagePath);
		
		Type t = typeDAO.findOne(type);
		if(type != null ) toEdit.setType(t);
		partDAO.save(toEdit);
		
		return Response.ok().build();
	}
	
	@POST
	@Path("/sellers/{seller}/parts/{part}/delete")
	public Response deletePart(@PathParam("seller") String username, @PathParam("part") Long id, @Context HttpServletRequest req ) {
		
		User user = userComp.find(username);
		userComp.checkSeller(user);
//		userComp.checkAuthorized(username, req);
		
		Part toEdit = partDAO.findOne(id);
		partDAO.delete(toEdit);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/admin/{username}/accounts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> viewAccounts(@PathParam("username") String username, @Context HttpServletRequest req) {
		User user = userComp.find(username);
		userComp.checkAdmin(user);
//		userComp.checkAuthorized(username, req);
		
		return userComp.viewAccounts();
	}
	
	@GET
	@Path("/admin/{username}/accounts/{account}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getAccount(@PathParam("username") String username, @PathParam("account") String name,
			@Context HttpServletRequest req) {
		User user = userComp.find(username);
		userComp.checkAdmin(user);
//		userComp.checkAuthorized(username, req);
	
		return userComp.getAccount(name);
	}
	
	@POST
	@Path("/admin/{username}/accounts/{account}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAccount(@PathParam("username") String username, @PathParam("account") String name,
			@Context HttpServletRequest req) {
		User user = userComp.find(username);
		userComp.checkAdmin(user);
//		userComp.checkAuthorized(username, req);
		
		userDAO.delete(user);
	
		return Response.ok().build();
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