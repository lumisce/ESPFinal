package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entities.Build;
import app.entities.BuildPart;
import app.entities.Part;

@Repository
public interface BuildPartRepository extends JpaRepository<BuildPart, Long>{
	List<BuildPart> findByBuild(Build b);
	BuildPart findByBuildAndPart(Build b, Part p);
}
