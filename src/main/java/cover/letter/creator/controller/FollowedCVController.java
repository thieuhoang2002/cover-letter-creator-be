package cover.letter.creator.controller;

import cover.letter.creator.model.FollowedCV;
import cover.letter.creator.service.FollowedCVService;
import cover.letter.creator.config.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow-cv")
public class FollowedCVController {

    private static final Logger logger = LoggerFactory.getLogger(FollowedCVController.class);

    @Autowired
    private FollowedCVService followedCVService;

    @Autowired
    private JwtUtil jwtUtil;

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