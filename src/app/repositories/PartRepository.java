package app.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Part;
import app.entities.Type;
import app.entities.User;

@Repository
public interface PartRepository extends JpaRepository<Part, Long>{
	Part findByName(String name);
	Page<Part> findByNameLike(String name, Pageable pg);
	Page<Part> findByType(Type t, Pageable pg);
	Page<Part> findByTypeAndNameLike(Type t, String name, Pageable pg);
	List<Part> findBySeller(User user);
}
