package cover.letter.creator.repository;

import cover.letter.creator.model.Template;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {
	List<Template> findByStatus(String status); 
	Optional<Template> findByName(String name);
	List<Template> findTop5ByStatusOrderByViewsDesc(String status);
	

}