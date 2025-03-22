package cover.letter.creator.repository;

import cover.letter.creator.model.Template;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {
	List<Template> findByStatus(String status); 
}