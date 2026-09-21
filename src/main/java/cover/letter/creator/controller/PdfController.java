package cover.letter.creator.controller;

import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.service.CoverLetterPdfService;
import cover.letter.creator.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CoverLetterPdfService coverLetterPdfService;

    /**
     * Sinh và stream trực tiếp file PDF về cho người dùng tải xuống máy
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePdf(@RequestBody PdfRequest request) {
        try {
            // Sinh PDF từ htmlContent
            byte[] pdfBytes = pdfService.generatePdfFromHtml(request.getHtmlContent());

            String templateName = request.getTemplateName() != null ? request.getTemplateName() : "CV";
            String emailPrefix = request.getEmail() != null ? request.getEmail().split("@")[0] : "user";
            String safeDate = request.getDate() != null ? request.getDate().replace("/", "-").replace(" ", "_") : "today";
            String fileName = templateName.replace(" ", "_") + "_" + emailPrefix + "_" + safeDate + ".pdf";

            // Tùy chọn lưu metadata vào DB nếu có userId
            if (request.getId() != null && !request.getId().isEmpty()) {
                try {
                    coverLetterPdfService.saveCoverLetterPdf(fileName, request.getId(), templateName);
                } catch (Exception ex) {
                    // Không ngắt tiến trình download nếu lưu DB gặp lỗi nhỏ
                }
            }

            // Stream trực tiếp file PDF về client
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating PDF: " + e.getMessage());
        }
    }

    // Load danh sách CoverLetterPdf theo userId
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<CoverLetterPdf>> getCoverLettersByUserId(@PathVariable String userId) {
        try {
            List<CoverLetterPdf> coverLetters = coverLetterPdfService.getCoverLettersByUserId(userId);
            return ResponseEntity.ok(coverLetters);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Xóa CoverLetterPdf theo id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoverLetterPdf(@PathVariable Integer id) {
        try {
            coverLetterPdfService.deleteCoverLetterPdf(id);
            return ResponseEntity.ok("Xoá file thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting cover letter PDF: " + e.getMessage());
        }
    }
}

// Lớp DTO để nhận request body
class PdfRequest {
    private String id;
    private String email;
    private String htmlContent;
    private String templateName;
    private String date;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}