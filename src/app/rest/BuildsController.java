package app.rest;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.components.UserComponent;
import app.entities.Build;
import app.entities.User;
import app.repositories.BuildRepository;
import app.repositories.UserRepository;

@Component
@Path("/u/{username}/build")
public class BuildsController extends AppController{

	//autowired components here including 3rd party client
	@Autowired
	UserRepository userDao;
	
	@Autowired
	UserComponent userComp;
	
	@Autowired
	BuildRepository buildDao;
	
	@POST
	@Path("/new")
	public Response createBuild(@PathParam("username") String username, @FormParam("name") String name, @Context HttpServletRequest req) {
		if (userComp.findUser(username) == null) {
			return Response.status(404).build();
		}
		if (userComp.isUnauthorized(username, req)) {
			return Response.status(403).build();
		}
		Build b = new Build();			// add parts
		b.setName(name);
		User u = userDao.findByUsername(username);
		b.setUser(u);
		buildDao.save(b);
		return Response.ok().build();
	}
	
	@GET
	@Path("/{build}")
	public Response viewBuild(@PathParam("username") String username, @PathParam("build") Long build) {
		Build b = buildDao.findById(build);
		if (userComp.findUser(username) == null || !b.getUser().getUsername().equals(username)) {
			return Response.status(404).build();
		}
		b.setCreated(formatDate(b.getCreatedAt()));
		return Response.ok().entity(b).build();
	}
	
	@GET
	@Path("/{build}/edit")
	public void editBuild(@PathParam("username") String username, @PathParam("build") String build) {
		// TODO return build and its parts
	}
	
	@POST
	@Path("/{build}/edit")
	public void updateBuild(@PathParam("username") String username, @PathParam("build") String build) {
		// TODO update build
	}
	
	@POST
	@Path("/{build}/delete")
	public void deleteBuild(@PathParam("username") String username, @PathParam("build") String build) {
		// TODO delete build
		// user.build.delete or build.delete?
	}
	
}