package cover.letter.creator.service;

import cover.letter.creator.model.ModernCVPdf;
import cover.letter.creator.model.TemplateModernCV;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.ModernCVPdfRepository;
import cover.letter.creator.repository.TemplateModernCVRepository;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ModernCVPdf saveModernCVPdf(String fileUrl, String userId, String templateName) {
        // Lấy User từ userId
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Lấy Template từ templateName
        TemplateModernCV template = templateModernCVRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with name: " + templateName));

        // Tạo đối tượng ModernCVPdf
        ModernCVPdf coverLetterPdf = new ModernCVPdf();
        coverLetterPdf.setUrlGoogleDrive(fileUrl != null ? fileUrl : "");
        coverLetterPdf.setUser(user);
        coverLetterPdf.setTemplateModernCV(template);
        coverLetterPdf.setCreatedAt(new Date());

        // Lưu vào database
        return modernCVPdfRepository.save(coverLetterPdf);
    }

    // Load danh sách ModernCVPdf theo userId
    public List<ModernCVPdf> getModernCVsByUserId(String userId) {
        Integer userIdInt = Integer.parseInt(userId);
        return modernCVPdfRepository.findAll().stream()
                .filter(pdf -> pdf.getUser().getId().equals(userIdInt))
                .toList();
    }

    // Xóa ModernCVPdf theo id
    @Transactional
    public void deleteModernCVPdf(Integer id) {
        modernCVPdfRepository.deleteById(id);
    }
}