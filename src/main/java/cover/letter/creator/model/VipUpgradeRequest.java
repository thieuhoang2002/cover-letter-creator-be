package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "vip_upgrade_requests")
@Data
public class VipUpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    /** Gói yêu cầu: "pro", "enterprise" */
    @Column(name = "plan", nullable = false)
    private String plan;

    /** Trạng thái: pending / approved / rejected */
    @Column(name = "status", nullable = false)
    private String status = "pending";

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "admin_note", length = 500)
    private String adminNote;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
