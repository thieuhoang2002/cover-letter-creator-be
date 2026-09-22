package cover.letter.creator.controller;

import cover.letter.creator.model.VipUpgradeRequest;
import cover.letter.creator.service.VipUpgradeService;
import cover.letter.creator.config.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class VipUpgradeController {

    private static final Logger logger = LoggerFactory.getLogger(VipUpgradeController.class);

    @Autowired
    private VipUpgradeService vipUpgradeService;

    @Autowired
    private JwtUtil jwtUtil;

    // ===== User endpoints =====

    /** POST /api/vip/request — User gửi yêu cầu nâng cấp */
    @PostMapping("/api/vip/request")
    public ResponseEntity<?> submitRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            String plan = body.getOrDefault("plan", "pro");
            String note = body.getOrDefault("note", "");
            VipUpgradeRequest req = vipUpgradeService.submitRequest(email, plan, note);
            return ResponseEntity.ok(Map.of("success", true, "message", "Yêu cầu nâng cấp VIP đã được gửi!", "data", req));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("success", false, "message", e.getReason()));
        } catch (Exception e) {
            logger.error("Error submitting VIP request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi gửi yêu cầu: " + e.getMessage()));
        }
    }

    /** GET /api/vip/my-status — User xem trạng thái yêu cầu của mình */
    @GetMapping("/api/vip/my-status")
    public ResponseEntity<?> getMyStatus(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            List<VipUpgradeRequest> requests = vipUpgradeService.getMyRequests(email);
            return ResponseEntity.ok(Map.of("success", true, "data", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ===== Admin endpoints =====

    /** GET /api/admin/vip-requests — Admin xem tất cả yêu cầu */
    @GetMapping("/api/admin/vip-requests")
    public ResponseEntity<?> getAllRequests(@RequestHeader("Authorization") String token) {
        try {
            List<VipUpgradeRequest> requests = vipUpgradeService.getAllRequests();
            return ResponseEntity.ok(Map.of("success", true, "data", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /** PUT /api/admin/vip-requests/{id}/approve — Admin duyệt */
    @PutMapping("/api/admin/vip-requests/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String adminNote = body != null ? body.getOrDefault("adminNote", "") : "";
            VipUpgradeRequest req = vipUpgradeService.approveRequest(id, adminNote);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã duyệt yêu cầu VIP!", "data", req));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("success", false, "message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /** PUT /api/admin/vip-requests/{id}/reject — Admin từ chối */
    @PutMapping("/api/admin/vip-requests/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String adminNote = body != null ? body.getOrDefault("adminNote", "") : "";
            VipUpgradeRequest req = vipUpgradeService.rejectRequest(id, adminNote);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã từ chối yêu cầu VIP!", "data", req));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("success", false, "message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
