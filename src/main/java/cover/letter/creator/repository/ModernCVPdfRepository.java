package cover.letter.creator.repository;

import cover.letter.creator.model.ModernCVPdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModernCVPdfRepository extends JpaRepository<ModernCVPdf, Integer> {
}