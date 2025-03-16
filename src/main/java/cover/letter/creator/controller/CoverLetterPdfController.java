package cover.letter.creator.controller;

import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.service.CoverLetterPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cover-letters")
@CrossOrigin 
public class CoverLetterPdfController {
    @Autowired
    private CoverLetterPdfService coverLetterPdfService;

    @GetMapping
    public List<CoverLetterPdf> getAllCoverLetters() {
        return coverLetterPdfService.getAllCoverLetters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoverLetterPdf> getCoverLetterById(@PathVariable Integer id) {
        Optional<CoverLetterPdf> coverLetter = coverLetterPdfService.getCoverLetterById(id);
        return coverLetter.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CoverLetterPdf createCoverLetter(@RequestBody CoverLetterPdf coverLetter) {
        return coverLetterPdfService.saveCoverLetter(coverLetter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoverLetterPdf> updateCoverLetter(@PathVariable Integer id, @RequestBody CoverLetterPdf coverLetter) {
        Optional<CoverLetterPdf> existingCoverLetter = coverLetterPdfService.getCoverLetterById(id);
        if (existingCoverLetter.isPresent()) {
            coverLetter.setId(id);
            return ResponseEntity.ok(coverLetterPdfService.saveCoverLetter(coverLetter));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoverLetter(@PathVariable Integer id) {
        if (coverLetterPdfService.getCoverLetterById(id).isPresent()) {
            coverLetterPdfService.deleteCoverLetter(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}