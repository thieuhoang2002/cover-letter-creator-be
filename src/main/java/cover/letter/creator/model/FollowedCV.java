package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "followed_cvs")
@Data
public class FollowedCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "url_google_drive", nullable = false)
    private String urlGoogleDrive;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "note")
    private String note;

    @Column(name = "company")
    private String company;

    @Column(name = "status", nullable = false)
    private String status = "pending";

    /**
     * Nguồn của CV: "system" (theo dõi bằng link) hoặc "uploaded" (người dùng tự upload file PDF)
     */
    @Column(name = "source")
    private String source = "system";

    /**
     * Dung lượng file (bytes) — chỉ áp dụng khi source = "uploaded"
     */
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}