package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "modern_cv_templates")
@Data
public class TemplateModernCV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // Ví dụ: "modern", "minimalist", "creative"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // HTML hoặc JSON template nội dung

    private String image; // Đường dẫn ảnh demo

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @Column(name = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'active'")
    private String status = "active";

    // Người dùng yêu thích mẫu này
    @ManyToMany(mappedBy = "lovedTemplatesModern")
    @JsonIgnore
    private Set<User> usersWhoLoved = new HashSet<>();

    // Trường tạm để đánh dấu là mẫu yêu thích hay không (không lưu DB)
    @Transient
    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateModernCV template = (TemplateModernCV) o;
        return id != null && id.equals(template.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
