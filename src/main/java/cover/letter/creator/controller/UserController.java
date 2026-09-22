package cover.letter.creator.controller;

import cover.letter.creator.config.JwtUtil;
import cover.letter.creator.dto.ChangePasswordRequest;
import cover.letter.creator.dto.ChangePasswordWithoutOldRequest;
import cover.letter.creator.dto.UserProfileDTO;
import cover.letter.creator.dto.UserProfileUpdateRequest;
import cover.letter.creator.model.Template;
import cover.letter.creator.model.User;
import cover.letter.creator.service.CloudflareR2Service;
import cover.letter.creator.service.TemplateService;
import cover.letter.creator.service.TemplateModernCVService;
import cover.letter.creator.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import cover.letter.creator.model.TemplateModernCV;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/users/profile")
@CrossOrigin 
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
    @Autowired
    private UserService userService;
    
    @Autowired
    private TemplateService templateService;
    
    @Autowired
    private TemplateModernCVService templateModernCVService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudflareR2Service cloudflareR2Service;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // ===== AVATAR UPLOAD =====
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate content type
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Chỉ chấp nhận định dạng .jpg, .png, .webp"));
            }
            // Validate file size (2MB max)
            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Ảnh không được vượt quá 2MB"));
            }

            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            // Build unique filename: avatars/{uuid}.{ext}
            String originalFilename = file.getOriginalFilename();
            String ext = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = "avatars/" + UUID.randomUUID() + ext;

            // Upload to R2
            String avatarUrl = cloudflareR2Service.uploadFile(fileName, file.getBytes(), contentType);

            // Update DB
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy người dùng"));
            }
            User user = userOpt.get();

            // Nếu user đã có avatar cũ lưu trên Cloudflare R2 (chứa /avatars/), xóa file cũ đi
            String oldAvatarUrl = user.getAvatarUrl();
            if (oldAvatarUrl != null && oldAvatarUrl.contains("/avatars/")) {
                try {
                    cloudflareR2Service.deleteFile(oldAvatarUrl);
                    logger.info("Deleted old R2 avatar for user '{}': {}", email, oldAvatarUrl);
                } catch (Exception ex) {
                    logger.warn("Could not delete old R2 avatar: {}", ex.getMessage());
                }
            }

            user.setAvatarUrl(avatarUrl);
            userService.saveUser(user);

            logger.info("Avatar updated for user '{}': {}", email, avatarUrl);
            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl, "message", "Cập nhật ảnh đại diện thành công!"));
        } catch (Exception e) {
            logger.error("Error uploading avatar: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi tải ảnh lên: " + e.getMessage()));
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
//        Optional<User> user = userService.getUserById(id);
//        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Integer id) {
        // Giả sử userId được sử dụng để lấy email hoặc bạn cần một cách khác để lấy email
        // Đây chỉ là ví dụ, bạn cần điều chỉnh logic lấy email dựa trên userId
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        UserProfileDTO userProfileDTO = userService.getUserProfileWithDetails(user.getEmail());
        return ResponseEntity.ok(userProfileDTO);
    }
    
    
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Integer id) {
        // Giả sử userId được sử dụng để lấy email hoặc bạn cần một cách khác để lấy email
        // Đây chỉ là ví dụ, bạn cần điều chỉnh logic lấy email dựa trên userId
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        UserProfileDTO userProfileDTO = userService.getUserProfileWithDetails(user.getEmail());
        return ResponseEntity.ok(userProfileDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
//    @GetMapping("/me")
//    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
//        try {
//            String jwt = token.replace("Bearer ", "");
//            String email = jwtUtil.extractEmail(jwt);
//            
//            // Lấy User + Loved Templates
//            Optional<User> user = userService.getUserByEmail(email);
//
//            if (user.isPresent()) {
//                logger.info("User: {}", user.get().getEmail());
//                logger.info("Loved Templates: {}", user.get().getLovedTemplates().size());
//                user.get().getLovedTemplates().forEach(t -> logger.info("Template: {}", t.getName()));
//
//                return ResponseEntity.ok(user.get());
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//            }
//        } catch (Exception e) {
//            logger.error("Error getting current user: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);


            UserProfileDTO userProfile = userService.getUserProfileWithDetails(email);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            logger.error("Error getting current user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/has-password")
    public ResponseEntity<Map<String, Boolean>> checkHasPassword(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            Optional<User> userOpt = userService.getUserByEmail(email);
            boolean hasPassword = userOpt.isPresent()
                    && userOpt.get().getPassword() != null
                    && !userOpt.get().getPassword().trim().isEmpty();
            return ResponseEntity.ok(Map.of("hasPassword", hasPassword));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("hasPassword", false));
        }
    }


    
    @PostMapping("/me/love-template/{templateId}")
    public ResponseEntity<String> toggleFavoriteTemplate(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer templateId) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            Optional<User> userOpt = userService.getUserByEmail(email);

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOpt.get();
            Optional<Template> templateOpt = templateService.getTemplateById(templateId);

            if (!templateOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template not found");
            }

            Template template = templateOpt.get();
            userService.toggleFavoriteTemplate(user, template);

            return ResponseEntity.ok("Favorite toggled successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error toggling favorite: " + e.getMessage());
        }
    }
    
    @PostMapping("/me/love-modern-template/{templateId}")
    public ResponseEntity<String> toggleFavoriteModernTemplate(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer templateId) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            Optional<User> userOpt = userService.getUserByEmail(email);

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOpt.get();
            Optional<TemplateModernCV> templateOpt = templateModernCVService.getTemplateById(templateId);

            if (!templateOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Modern template not found");
            }

            TemplateModernCV template = templateOpt.get();
            userService.toggleFavoriteModernTemplate(user, template);

            return ResponseEntity.ok("Modern favorite toggled successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error toggling modern favorite: " + e.getMessage());
        }
    }

    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request) {

        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            boolean success = userService.changePassword(email, request.getOldPassword(), request.getNewPassword());

            return success
                    ? ResponseEntity.ok("Đổi mật khẩu thành công!")
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu cũ sai!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đổi mật khẩu!");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUserProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            User updatedUser = userService.updateUserProfile(email, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/change-password-without-old")
    public ResponseEntity<String> changePasswordWithoutOld(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordWithoutOldRequest request) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            userService.changePasswordWithoutOld(email, request.getNewPassword());

            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error changing password without old: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đổi mật khẩu!");
        }
    }
}

