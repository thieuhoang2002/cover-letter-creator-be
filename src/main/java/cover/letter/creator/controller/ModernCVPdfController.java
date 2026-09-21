package cover.letter.creator.controller;

import cover.letter.creator.model.ModernCVPdf;
import cover.letter.creator.service.ModernCVPdfService;
import cover.letter.creator.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modern-cv/pdf")
@CrossOrigin
public class ModernCVPdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ModernCVPdfService modernCVPdfService;

    /**
     * Sinh và stream trực tiếp file PDF về cho người dùng tải xuống máy
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePdf(@RequestBody ModernCVPdfRequest request) {
        try {
            // Sinh PDF từ htmlContent
            byte[] pdfBytes = pdfService.generatePdfFromHtml(request.getHtmlContent());

            String templateName = request.getTemplateName() != null ? request.getTemplateName() : "Modern_CV";
            String emailPrefix = request.getEmail() != null ? request.getEmail().split("@")[0] : "user";
            String safeDate = request.getDate() != null ? request.getDate().replace("/", "-").replace(" ", "_") : "today";
            String fileName = templateName.replace(" ", "_") + "_" + emailPrefix + "_" + safeDate + ".pdf";

            if (request.getId() != null && !request.getId().isEmpty()) {
                try {
                    modernCVPdfService.saveModernCVPdf(fileName, request.getId(), templateName);
                } catch (Exception ex) {
                    // ignore
                }
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating PDF: " + e.getMessage());
        }
    }

    // Load danh sách ModernCVPdf theo userId
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<ModernCVPdf>> getModernCVsByUserId(@PathVariable String userId) {
        try {
            List<ModernCVPdf> coverLetters = modernCVPdfService.getModernCVsByUserId(userId);
            return ResponseEntity.ok(coverLetters);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Xóa ModernCVPdf theo id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteModernCVPdf(@PathVariable Integer id) {
        try {
            modernCVPdfService.deleteModernCVPdf(id);
            return ResponseEntity.ok("Xoá file thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting cover letter PDF: " + e.getMessage());
        }
    }
}

// Lớp DTO để nhận request body
class ModernCVPdfRequest {
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