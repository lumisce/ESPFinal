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
import app.components.ChikkaClient;
import app.components.UserComponent;
import app.entities.Build;
import app.entities.BuildPart;
import app.entities.Part;
import app.entities.User;
import app.repositories.BuildPartRepository;
import app.repositories.BuildRepository;
import app.repositories.PartRepository;

@Component
@Path("/users/{username}/builds")
public class BuildsController extends AppController{

	//autowired components here including 3rd party client
	
	@Autowired
	UserComponent userComp;
	
	@Autowired
	BuildComponent buildComp;
	
	@Autowired
	PartRepository partDAO;
	
	@Autowired
	BuildRepository buildDAO;
	
	@Autowired
	BuildPartRepository bpDAO;
	
	@Autowired
	ChikkaClient chikka;
	
	@POST
	@Path("/new")
	public Response createBuild(@PathParam("username") String username, @FormParam("name") String name, @Context HttpServletRequest req) {
//		userComp.checkAuthorized(username, req);
		User u = userComp.find(username);
		Build b = buildComp.create(name, u);
		return Response.ok(b.getId()).build();
	}
	
	@GET
	@Path("/{build}")
	public Response viewBuild(@PathParam("username") String username, @PathParam("build") Long build) {
		Build b = buildComp.find(build);
		if (!username.equals(b.getUser().getUsername())) {
			throw new WebApplicationException(404);
		}
		b.setCreated(formatDate(b.getCreatedAt()));
		b.setUsername(b.getUser().getUsername());
		b.setbParts(bpDAO.findByBuild(b));
		return Response.ok().entity(b).build();
	}
	
	@POST
	@Path("/{build}/sendText")
	public Response textBuild(@PathParam("username") String username, @PathParam("build") Long build) {
		String phone = userComp.find(username).getPhoneNumber();
		Build b = buildComp.find(build);
		if (!username.equals(b.getUser().getUsername())) {
			throw new WebApplicationException(404);
		}
		b.setCreated(formatDate(b.getCreatedAt()));
		b.setUsername(b.getUser().getUsername());
		b.setbParts(bpDAO.findByBuild(b));
		String message = "Build: "+b.getName()+"\ncreated by: "+b.getUsername()+"\nParts:";
		for (BuildPart bp : b.getbParts()) {
			message += "\n"+bp.getPart().getName()+" (" + bp.getPart().getPrice() +")";
		}
		chikka.sendMessage(phone, message);
		return Response.ok().build();
	}
	
	@POST
	@Path("/{build}/edit")
	public Response updateBuild(@PathParam("username") String username, @PathParam("build") Long build, 
			@FormParam("name") String name, @Context HttpServletRequest req) {
		
//		userComp.checkAuthorized(username, req);
		
		Build b = buildComp.find(build);
		if (!username.equals(b.getUser().getUsername())) {
			throw new WebApplicationException(404);
		}
		b.setName(name);
		buildDAO.save(b);
		return Response.ok(b).build();
	}
	
	
	@POST
	@Path("{build}/addPart")
	public Response addPartToBuild(@PathParam("username") String username, @PathParam("build") Long build, @FormParam("part_id") Long part,  @Context HttpServletRequest req) {
//		userComp.checkAuthorized(username, req);
		
		Build b = buildComp.find(build);
		if (!username.equals(b.getUser().getUsername())) {
			throw new WebApplicationException(404);
		}
		Part p = partDAO.findOne(part);
		BuildPart bp = new BuildPart();
		bp.setBuild(b);
		bp.setPart(p);
		bpDAO.save(bp);
		return Response.ok().build();
	}
	
	@POST
	@Path("{build}/removePart")
	public Response removePartFromBuild(@PathParam("username") String username, @PathParam("build") Long build, @FormParam("part_id") Long part,  @Context HttpServletRequest req) {
//		userComp.checkAuthorized(username, req);
		
		Build b = buildComp.find(build);
		if (!username.equals(b.getUser().getUsername())) {
			throw new WebApplicationException(404);
		}
		Part p = partDAO.findOne(part);
		BuildPart bp = bpDAO.findByBuildAndPart(b, p);
		bpDAO.delete(bp);
		return Response.ok().build();
	}
	
	@POST
	@Path("/{build}/delete")
	public Response deleteBuild(@PathParam("username") String username, @PathParam("build") Long build, @Context HttpServletRequest req) {
//		userComp.checkAuthorized(username, req);
		
		buildDAO.delete(build);
		return Response.ok().build();
	}
	
}