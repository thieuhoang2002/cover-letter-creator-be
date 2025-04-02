package cover.letter.creator.service;

import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.model.Template;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.CoverLetterPdfRepository;
import cover.letter.creator.repository.TemplateRepository;
import cover.letter.creator.repository.UserRepository;
import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class CoverLetterPdfService {

    @Autowired
    private CoverLetterPdfRepository coverLetterPdfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private Drive driveService; // Tiêm Drive để thao tác với Google Drive

    @Transactional
    public CoverLetterPdf saveCoverLetterPdf(String fileId, String userId, String templateName) {
        // Tạo URL Google Drive từ fileId
        String googleDriveUrl = "https://drive.google.com/file/d/" + fileId + "/view";

        // Lấy User từ userId
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Lấy Template từ templateName
        Template template = templateRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with name: " + templateName));

        // Tạo đối tượng CoverLetterPdf
        CoverLetterPdf coverLetterPdf = new CoverLetterPdf();
        coverLetterPdf.setUrlGoogleDrive(googleDriveUrl);
        coverLetterPdf.setUser(user);
        coverLetterPdf.setTemplate(template);
        coverLetterPdf.setCreatedAt(new Date());

        // Lưu vào database
        return coverLetterPdfRepository.save(coverLetterPdf);
    }

    // Load danh sách CoverLetterPdf theo userId
    public List<CoverLetterPdf> getCoverLettersByUserId(String userId) {
        Integer userIdInt = Integer.parseInt(userId);
        return coverLetterPdfRepository.findAll().stream()
                .filter(pdf -> pdf.getUser().getId().equals(userIdInt))
                .toList();
    }

    // Xóa CoverLetterPdf theo id
    @Transactional
    public void deleteCoverLetterPdf(Integer id) throws IOException {
        // Tìm bản ghi trong database
        CoverLetterPdf coverLetterPdf = coverLetterPdfRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CoverLetterPdf not found with ID: " + id));

        // Trích xuất fileId từ URL Google Drive
        String fileId = extractFileIdFromUrl(coverLetterPdf.getUrlGoogleDrive());

        // Xóa file trên Google Drive
        driveService.files().delete(fileId).execute();

        // Xóa bản ghi trong database
        coverLetterPdfRepository.deleteById(id);
    }

    // Hàm hỗ trợ để trích xuất fileId từ URL Google Drive
    private String extractFileIdFromUrl(String url) {
        // URL dạng: https://drive.google.com/file/d/<fileId>/view
        String[] parts = url.split("/d/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid Google Drive URL: " + url);
        }
        String filePart = parts[1];
        return filePart.split("/")[0]; // Lấy fileId
    }
}