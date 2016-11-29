package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Type;
import app.entities.User;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long>{
	Type findByName(String name);
}
