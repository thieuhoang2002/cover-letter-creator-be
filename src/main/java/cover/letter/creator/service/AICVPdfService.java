package cover.letter.creator.service;

import cover.letter.creator.model.AICVPdf;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.AICVPdfRepository;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AICVPdfService {

    @Autowired
    private AICVPdfRepository aicvPdfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudflareR2Service cloudflareR2Service;

    @Transactional
    public AICVPdf saveAICVPdf(String fileName, byte[] pdfBytes, String userId, String templateName) {
        String fileUrl = cloudflareR2Service.uploadPdf(fileName, pdfBytes);

        // Lấy User từ userId
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Chống lưu trùng lặp nếu người dùng click liên tục trong vòng 10 giây
        Date tenSecondsAgo = new Date(System.currentTimeMillis() - 10000);
        boolean isDuplicate = aicvPdfRepository.findAll().stream()
                .anyMatch(pdf -> pdf.getUser() != null
                        && pdf.getUser().getId().equals(user.getId())
                        && fileUrl.equals(pdf.getUrlGoogleDrive())
                        && pdf.getCreatedAt() != null
                        && pdf.getCreatedAt().after(tenSecondsAgo));
        if (isDuplicate) {
            return null;
        }

        // Tạo đối tượng AICVPdf
        AICVPdf coverLetterPdf = new AICVPdf();
        coverLetterPdf.setUrlGoogleDrive(fileUrl);
        coverLetterPdf.setUser(user);
        coverLetterPdf.setCreatedAt(new Date());

        // Lưu vào database
        return aicvPdfRepository.save(coverLetterPdf);
    }

    // Load danh sách AICVPdf theo userId
    public List<AICVPdf> getAICVsByUserId(String userId) {
        Integer userIdInt = Integer.parseInt(userId);
        return aicvPdfRepository.findAll().stream()
                .filter(pdf -> pdf.getUser().getId().equals(userIdInt))
                .toList();
    }

    // Xóa AICVPdf theo id
    @Transactional
    public void deleteAICVPdf(Integer id) {
        aicvPdfRepository.findById(id).ifPresent(pdf -> {
            cloudflareR2Service.deleteFile(pdf.getUrlGoogleDrive());
            aicvPdfRepository.deleteById(id);
        });
    }
}