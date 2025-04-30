package cover.letter.creator.controller;

import cover.letter.creator.service.PdfService;
import cover.letter.creator.service.CoverLetterPdfService;
import cover.letter.creator.service.GoogleDriveService;
import cover.letter.creator.service.ModernCVPdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.model.ModernCVPdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/modern-cv/pdf")
@CrossOrigin
public class ModernCVPdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private GoogleDriveService googleDriveService;
    
    @Autowired
    private ModernCVPdfService modernCVPdfService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePdf(@RequestBody ModernCVPdfRequest request) {
        try {
            // Sinh PDF từ htmlContent
            byte[] pdfBytes = pdfService.generatePdfFromHtml(request.getHtmlContent());

            // Lấy id và email từ request
            String id = request.getId();
            String email = request.getEmail();
            String templateName = request.getTemplateName();
            String date = request.getDate();

            // Tạo tên file: id + email (chỉ lấy phần trước @)
            String emailPrefix = email.split("@")[0];
            String safeDate = date.replace("/", "-").replace(" ", "_"); // Chuyển đổi ngày thành định dạng hợp lệ
            String fileName = templateName.replace(" ", "_") + "_" + emailPrefix + "_" + safeDate + "_ID" + id + ".pdf";

            // Chuyển byte[] thành MultipartFile để upload
            MultipartFile multipartFile = new CustomMultipartFileModernCV(fileName, pdfBytes);


            // Upload file lên Google Drive
            String fileId = googleDriveService.uploadFile(multipartFile);

            // Lưu thông tin file vào database
            modernCVPdfService.saveModernCVPdf(fileId, id, templateName);
            System.out.print("TOI DAY ROI NE");


            // Trả về File ID
            return ResponseEntity.ok("File upload thành công lên Google Drive. File ID: " + fileId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating or uploading PDF: " + e.getMessage());
        }
    }
    
  //Load danh sách CoverLetterPdf theo userId
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<ModernCVPdf>> getModernCVsByUserId(@PathVariable String userId) {
        try {
            List<ModernCVPdf> coverLetters = modernCVPdfService.getModernCVsByUserId(userId);
            return ResponseEntity.ok(coverLetters);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
 // Xóa CoverLetterPdf theo id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteModernCVPdf(@PathVariable Integer id) {
        try {
        	modernCVPdfService.deleteModernCVPdf(id);
            return ResponseEntity.ok("Xoá file thành công!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting file from Google Drive: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting cover letter PDF: " + e.getMessage());
        }
    }
}

//Lớp DTO để nhận request body
class ModernCVPdfRequest {
 private String id;
 private String email;
 private String htmlContent;
 private String templateName;
 private String date;

 public String getId() {
     return id;
 }

 public void setId(String id) {
     this.id = id;
 }

 public String getEmail() {
     return email;
 }

 public void setEmail(String email) {
     this.email = email;
 }

 public String getHtmlContent() {
     return htmlContent;
 }

 public void setHtmlContent(String htmlContent) {
     this.htmlContent = htmlContent;
 }

 public String getTemplateName() {
     return templateName;
 }

 public void setTemplateName(String templateName) {
     this.templateName = templateName;
 }

 public String getDate() {
     return date;
 }

 public void setDate(String date) {
     this.date = date;
 }
}

// Lớp hỗ trợ để chuyển byte[] thành MultipartFile
class CustomMultipartFileModernCV implements MultipartFile {
    private final String name;
    private final byte[] content;

    public CustomMultipartFileModernCV(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return name;
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        // Không cần implement trong trường hợp này
    }
}