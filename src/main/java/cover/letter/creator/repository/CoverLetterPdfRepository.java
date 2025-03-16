package cover.letter.creator.repository;

import cover.letter.creator.model.CoverLetterPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverLetterPdfRepository extends JpaRepository<CoverLetterPdf, Integer> {
}