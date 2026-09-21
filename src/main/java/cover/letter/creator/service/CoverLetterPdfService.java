package cover.letter.creator.service;

import cover.letter.creator.model.CoverLetterPdf;
import cover.letter.creator.model.Template;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.CoverLetterPdfRepository;
import cover.letter.creator.repository.TemplateRepository;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private CloudflareR2Service cloudflareR2Service;

    @Transactional
    public CoverLetterPdf saveCoverLetterPdf(String fileName, byte[] pdfBytes, String userId, String templateName) {
        // Upload lên Cloudflare R2 để lấy URL lưu trữ
        String fileUrl = cloudflareR2Service.uploadPdf(fileName, pdfBytes);

        // Lấy User từ userId
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Lấy Template từ templateName
        Template template = templateRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with name: " + templateName));

        // Tạo đối tượng CoverLetterPdf
        CoverLetterPdf coverLetterPdf = new CoverLetterPdf();
        coverLetterPdf.setUrlGoogleDrive(fileUrl);
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
    public void deleteCoverLetterPdf(Integer id) {
        coverLetterPdfRepository.findById(id).ifPresent(pdf -> {
            cloudflareR2Service.deleteFile(pdf.getUrlGoogleDrive());
            coverLetterPdfRepository.deleteById(id);
        });
    }
}