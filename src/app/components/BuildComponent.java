package app.components;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entities.Build;
import app.entities.Part;
import app.entities.User;
import app.repositories.BuildRepository;

@Component
public class BuildComponent {
	
	@Autowired
	BuildRepository dao;
	
	public Build find(Long id) {
		Build b = dao.findById(id);
		if (b == null) {
			throw new WebApplicationException(404);
		}
		return b;
	}
	
	public void create(String name, User u, List<Part> parts) {
		Build b = new Build();			// add parts
		b.setName(name);
		b.setUser(u);
		dao.save(b);
	}
}
