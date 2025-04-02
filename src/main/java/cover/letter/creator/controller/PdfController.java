package cover.letter.creator.controller;

import cover.letter.creator.service.PdfService;
import cover.letter.creator.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePdf(@RequestBody PdfRequest request) {
        try {
            // Sinh PDF từ htmlContent
            byte[] pdfBytes = pdfService.generatePdfFromHtml(request.getHtmlContent());

            // Lấy id và email từ request
            String id = request.getId();
            String email = request.getEmail();

            // Tạo tên file: id + email (chỉ lấy phần trước @)
            String emailPrefix = email.split("@")[0];
            String fileName = id + emailPrefix + ".pdf";

            // Chuyển byte[] thành MultipartFile để upload
            MultipartFile multipartFile = new CustomMultipartFile(fileName, pdfBytes);

            // Upload file lên Google Drive
            String fileId = googleDriveService.uploadFile(multipartFile);

            // Trả về File ID
            return ResponseEntity.ok("File uploaded successfully to Google Drive. File ID: " + fileId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating or uploading PDF: " + e.getMessage());
        }
    }
}

// Lớp DTO để nhận request body
class PdfRequest {
    private String id;
    private String email;
    private String htmlContent;

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
}

// Lớp hỗ trợ để chuyển byte[] thành MultipartFile
class CustomMultipartFile implements MultipartFile {
    private final String name;
    private final byte[] content;

    public CustomMultipartFile(String name, byte[] content) {
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