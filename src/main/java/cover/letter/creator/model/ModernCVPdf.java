package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

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

    @JsonIgnoreProperties({"coverLetters"})
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnoreProperties({"usersWhoLoved"})
    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private TemplateModernCV templateModernCV;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUrlGoogleDrive() { return urlGoogleDrive; }
    public void setUrlGoogleDrive(String urlGoogleDrive) { this.urlGoogleDrive = urlGoogleDrive; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TemplateModernCV getTemplateModernCV() { return templateModernCV; }
    public void setTemplateModernCV(TemplateModernCV templateModernCV) { this.templateModernCV = templateModernCV; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}