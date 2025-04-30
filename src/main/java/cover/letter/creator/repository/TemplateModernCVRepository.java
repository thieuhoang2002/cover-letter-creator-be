package cover.letter.creator.repository;

import cover.letter.creator.model.TemplateModernCV;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TemplateModernCVRepository extends JpaRepository<TemplateModernCV, Integer> {
	List<TemplateModernCV> findByStatus(String status); 
	Optional<TemplateModernCV> findByName(String name);
	List<TemplateModernCV> findTop5ByStatusOrderByViewsDesc(String status);
	

}