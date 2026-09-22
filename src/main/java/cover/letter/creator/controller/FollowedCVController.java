package cover.letter.creator.controller;

import cover.letter.creator.model.FollowedCV;
import cover.letter.creator.model.User;
import cover.letter.creator.service.CloudflareR2Service;
import cover.letter.creator.service.FollowedCVService;
import cover.letter.creator.config.JwtUtil;
import cover.letter.creator.repository.FollowedCVRepository;
import cover.letter.creator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/follow-cv")
public class FollowedCVController {

    private static final Logger logger = LoggerFactory.getLogger(FollowedCVController.class);
    private static final int FREE_UPLOAD_QUOTA = 3;

    @Autowired
    private FollowedCVService followedCVService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudflareR2Service cloudflareR2Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowedCVRepository followedCVRepository;

    // ===== UPLOAD PDF CV TỪ MÁY =====
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCvPdf(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "note", required = false) String note) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (!"application/pdf".equalsIgnoreCase(contentType)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Chỉ chấp nhận file PDF", null));
            }
            // Validate size (10MB max)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "File PDF không được vượt quá 10MB", null));
            }

            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Check VIP quota (only for non-VIP users)
            boolean isVip = "vip".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getRole());
            if (!isVip) {
                long uploadedCount = followedCVRepository.countUploadedByUserId(user.getId());
                if (uploadedCount >= FREE_UPLOAD_QUOTA) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponse(false,
                                    "Tài khoản Free chỉ được upload tối đa " + FREE_UPLOAD_QUOTA + " file CV. Vui lòng nâng cấp VIP để tiếp tục!",
                                    Map.of("quotaExceeded", true, "uploadedCount", uploadedCount, "maxQuota", FREE_UPLOAD_QUOTA)));
                }
            }

            // Upload to R2: customer-cvs/{userId}/{uuid}.pdf
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "cv.pdf";
            String fileName = "customer-cvs/" + user.getId() + "/" + UUID.randomUUID() + ".pdf";
            String fileUrl = cloudflareR2Service.uploadFile(fileName, file.getBytes(), "application/pdf");

            // Save record
            FollowedCV cv = new FollowedCV();
            cv.setUserId(user.getId());
            cv.setUrlGoogleDrive(fileUrl);
            cv.setName(name != null && !name.isBlank() ? name : originalName.replace(".pdf", ""));
            cv.setCompany(company);
            cv.setNote(note);
            cv.setStatus("pending");
            cv.setSource("uploaded");
            cv.setFileSize(file.getSize());
            FollowedCV saved = followedCVService.addFollowedCVDirect(cv);

            return ResponseEntity.ok(new ApiResponse(true, "Tải lên CV thành công!", saved));
        } catch (Exception e) {
            logger.error("Error uploading CV PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi khi tải lên CV: " + e.getMessage(), null));
        }
    }

    /** GET /api/follow-cv/quota — Kiểm tra quota upload còn lại */
    @GetMapping("/quota")
    public ResponseEntity<?> getUploadQuota(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            boolean isVip = "vip".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getRole());
            long used = followedCVRepository.countUploadedByUserId(user.getId());
            return ResponseEntity.ok(Map.of(
                    "isVip", isVip,
                    "used", used,
                    "max", isVip ? -1 : FREE_UPLOAD_QUOTA,
                    "remaining", isVip ? -1 : Math.max(0, FREE_UPLOAD_QUOTA - used)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addFollowedCV(
            @RequestHeader("Authorization") String token,
            @RequestBody FollowedCV followedCV) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            FollowedCV savedCV = followedCVService.addFollowedCV(email, followedCV);
            return ResponseEntity.ok().body(new ApiResponse(true, "Thêm CV theo dõi thành công", savedCV));
        } catch (Exception e) {
            logger.error("Error adding followed CV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi khi thêm CV theo dõi: " + e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getFollowedCVsByUser(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            List<FollowedCV> followedCVs = followedCVService.getFollowedCVsByUserEmail(email);
            return ResponseEntity.ok().body(new ApiResponse(true, "Lấy danh sách CV theo dõi thành công", followedCVs));
        } catch (Exception e) {
            logger.error("Error fetching followed CVs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi khi lấy danh sách CV theo dõi: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFollowedCV(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody FollowedCV updatedCV) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            FollowedCV updated = followedCVService.updateFollowedCV(id, email, updatedCV);
            return ResponseEntity.ok().body(new ApiResponse(true, "Cập nhật CV theo dõi thành công", updated));
        } catch (Exception e) {
            logger.error("Error updating followed CV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi khi cập nhật CV theo dõi: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFollowedCV(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            followedCVService.deleteFollowedCV(id, email);
            return ResponseEntity.ok().body(new ApiResponse(true, "Xóa CV theo dõi thành công", null));
        } catch (Exception e) {
            logger.error("Error deleting followed CV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Lỗi khi xóa CV theo dõi: " + e.getMessage(), null));
        }
    }

    // Class hỗ trợ định dạng response
    private static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}