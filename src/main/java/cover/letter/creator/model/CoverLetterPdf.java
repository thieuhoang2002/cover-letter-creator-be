package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cover_letters_pdf")
@Data
public class CoverLetterPdf {
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
    private Template template;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUrlGoogleDrive() { return urlGoogleDrive; }
    public void setUrlGoogleDrive(String urlGoogleDrive) { this.urlGoogleDrive = urlGoogleDrive; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Template getTemplate() { return template; }
    public void setTemplate(Template template) { this.template = template; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}