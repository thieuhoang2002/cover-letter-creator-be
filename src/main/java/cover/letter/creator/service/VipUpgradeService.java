package cover.letter.creator.service;

import cover.letter.creator.model.User;
import cover.letter.creator.model.VipUpgradeRequest;
import cover.letter.creator.repository.UserRepository;
import cover.letter.creator.repository.VipUpgradeRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VipUpgradeService {

    private static final Logger logger = LoggerFactory.getLogger(VipUpgradeService.class);

    @Autowired
    private VipUpgradeRequestRepository vipRepository;

    @Autowired
    private UserRepository userRepository;

    public VipUpgradeRequest submitRequest(String email, String plan, String note) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        // Only allow 1 pending request at a time
        long pendingCount = vipRepository.countByUserIdAndStatus(user.getId(), "pending");
        if (pendingCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bạn đã có yêu cầu nâng cấp đang chờ xử lý. Vui lòng chờ Admin phê duyệt.");
        }

        VipUpgradeRequest req = new VipUpgradeRequest();
        req.setUserId(user.getId());
        req.setUserEmail(email);
        req.setPlan(plan);
        req.setNote(note);
        req.setStatus("pending");
        req.setCreatedAt(LocalDateTime.now());

        return vipRepository.save(req);
    }

    public List<VipUpgradeRequest> getMyRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        return vipRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<VipUpgradeRequest> getAllRequests() {
        return vipRepository.findAllByOrderByCreatedAtDesc();
    }

    public VipUpgradeRequest approveRequest(Long id, String adminNote) {
        VipUpgradeRequest req = vipRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu"));
        req.setStatus("approved");
        req.setAdminNote(adminNote);
        req.setUpdatedAt(LocalDateTime.now());

        // Grant VIP role to user
        userRepository.findById(req.getUserId()).ifPresent(user -> {
            user.setRole("vip");
            userRepository.save(user);
            logger.info("User {} upgraded to VIP (plan: {})", user.getEmail(), req.getPlan());
        });

        return vipRepository.save(req);
    }

    public VipUpgradeRequest rejectRequest(Long id, String adminNote) {
        VipUpgradeRequest req = vipRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu"));
        req.setStatus("rejected");
        req.setAdminNote(adminNote);
        req.setUpdatedAt(LocalDateTime.now());
        return vipRepository.save(req);
    }
}
