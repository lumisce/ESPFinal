package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Part;
import app.entities.Type;
import app.entities.User;

@Repository
public interface PartRepository extends JpaRepository<Part, Long>{
	Part findByName(String name);
	List<Part> findByNameLike(String name);
	List<Part> findByType(Type t);
	List<Part> findByTypeAndNameLike(Type t, String name);
	List<Part> findBySeller(User user);
}
