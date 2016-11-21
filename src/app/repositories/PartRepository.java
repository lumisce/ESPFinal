package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Part;
import app.entities.User;

@Repository
public interface PartRepository extends JpaRepository<Part, Long>{
	Part findByName(String name);
}
