package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "modern_cv_pdf")
@Data
public class ModernCVPdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url_google_drive", nullable = false)
    private String urlGoogleDrive;

    @JsonIgnoreProperties({"coverLetters"}) // Bỏ qua danh sách coverLetters trong User để tránh vòng lặp
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnoreProperties({"usersWhoLoved"}) // Bỏ qua danh sách người dùng yêu thích để tránh vòng lặp
    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private TemplateModernCV templateModernCV;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}