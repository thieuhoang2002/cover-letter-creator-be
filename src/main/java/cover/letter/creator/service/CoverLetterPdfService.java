package cover.letter.creator.service;

import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.repository.CoverLetterPdfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoverLetterPdfService {
    @Autowired
    private CoverLetterPdfRepository coverLetterPdfRepository;

    public List<CoverLetterPdf> getAllCoverLetters() {
        return coverLetterPdfRepository.findAll();
    }

    public Optional<CoverLetterPdf> getCoverLetterById(Integer id) {
        return coverLetterPdfRepository.findById(id);
    }

    public CoverLetterPdf saveCoverLetter(CoverLetterPdf coverLetter) {
        return coverLetterPdfRepository.save(coverLetter);
    }

    public void deleteCoverLetter(Integer id) {
        coverLetterPdfRepository.deleteById(id);
    }
}