package cover.letter.creator.repository;

import cover.letter.creator.model.AICVPdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AICVPdfRepository extends JpaRepository<AICVPdf, Integer> {
}