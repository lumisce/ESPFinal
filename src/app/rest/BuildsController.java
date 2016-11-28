package app.rest;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.components.BuildComponent;
import app.components.UserComponent;
import app.entities.Build;
import app.entities.User;

@Component
@Path("/users/{username}/builds")
public class BuildsController extends AppController{

	//autowired components here including 3rd party client
	
	@Autowired
	UserComponent userComp;
	
	@Autowired
	BuildComponent buildComp;
	
	@POST
	@Path("/new")
	public Response createBuild(@PathParam("username") String username, @FormParam("name") String name, @Context HttpServletRequest req) {
		userComp.checkAuthorized(username, req);
		
		User u = userComp.find(username);
		buildComp.create(name, u, new ArrayList<>());
		return Response.ok().build();
	}
	
	@GET
	@Path("/{build}")
	public Response viewBuild(@PathParam("username") String username, @PathParam("build") Long build) {
		Build b = buildComp.find(build);
		if (userComp.find(username) != b.getUser()) {
			throw new WebApplicationException(404);
		}
		b.setCreated(formatDate(b.getCreatedAt()));
		// TODO b.setparts
		return Response.ok().entity(b).build();
	}
	
	@POST
	@Path("/{build}/edit")
	public Response updateBuild(@PathParam("username") String username, @PathParam("build") Long build, @Context HttpServletRequest req) {
		userComp.checkAuthorized(username, req);
		
		Build b = buildComp.find(build);
		if (userComp.find(username) != b.getUser()) {
			throw new WebApplicationException(404);
		}
		// TODO b.setParts
		return Response.ok().build();
	}
	
	@POST
	@Path("/{build}/delete")
	public void deleteBuild(@PathParam("username") String username, @PathParam("build") Long build, @Context HttpServletRequest req) {
		userComp.checkAuthorized(username, req);
		// TODO delete build
		// user.build.delete and build.delete?
	}
	
}