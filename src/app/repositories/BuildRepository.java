package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Build;
import app.entities.User;

@Repository
public interface BuildRepository extends JpaRepository<Build, Long>{
	List<Build> findByUser(User user);
	Build findById(Long id);
}
