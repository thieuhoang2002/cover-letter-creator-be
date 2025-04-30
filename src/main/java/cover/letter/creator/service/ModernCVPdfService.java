package cover.letter.creator.service;

import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.model.ModernCVPdf;
import cover.letter.creator.model.Template;
import cover.letter.creator.model.TemplateModernCV;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.CoverLetterPdfRepository;
import cover.letter.creator.repository.ModernCVPdfRepository;
import cover.letter.creator.repository.TemplateModernCVRepository;
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
public class ModernCVPdfService {

    @Autowired
    private ModernCVPdfRepository modernCVPdfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemplateModernCVRepository templateModernCVRepository;

    @Autowired
    private Drive driveService; // Tiêm Drive để thao tác với Google Drive

    @Transactional
    public ModernCVPdf saveModernCVPdf(String fileId, String userId, String templateName) {

        // Tạo URL Google Drive từ fileId
        String googleDriveUrl = "https://drive.google.com/file/d/" + fileId + "/view";

        // Lấy User từ userId
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        


        // Lấy Template từ templateName
        TemplateModernCV template = templateModernCVRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with name: " + templateName));
        
        System.out.print("TOI DAY ROI NE");

        // Tạo đối tượng CoverLetterPdf
        ModernCVPdf coverLetterPdf = new ModernCVPdf();
        coverLetterPdf.setUrlGoogleDrive(googleDriveUrl);
        coverLetterPdf.setUser(user);
        coverLetterPdf.setTemplateModernCV(template);
        coverLetterPdf.setCreatedAt(new Date());

        // Lưu vào database
        return modernCVPdfRepository.save(coverLetterPdf);
    }

    // Load danh sách CoverLetterPdf theo userId
    public List<ModernCVPdf> getModernCVsByUserId(String userId) {
        Integer userIdInt = Integer.parseInt(userId);
        return modernCVPdfRepository.findAll().stream()
                .filter(pdf -> pdf.getUser().getId().equals(userIdInt))
                .toList();
    }

    // Xóa CoverLetterPdf theo id
    @Transactional
    public void deleteModernCVPdf(Integer id) throws IOException {
        // Tìm bản ghi trong database
    	ModernCVPdf coverLetterPdf = modernCVPdfRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CoverLetterPdf not found with ID: " + id));

        // Trích xuất fileId từ URL Google Drive
        String fileId = extractFileIdFromUrl(coverLetterPdf.getUrlGoogleDrive());

        // Xóa file trên Google Drive
        driveService.files().delete(fileId).execute();

        // Xóa bản ghi trong database
        modernCVPdfRepository.deleteById(id);
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