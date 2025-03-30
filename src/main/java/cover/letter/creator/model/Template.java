package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "templates")
@Data
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String image;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'active'")
    private String status = "active";
    
    @ManyToMany(mappedBy = "lovedTemplates")
    @JsonIgnore // Ngăn Jackson serialize usersWhoLoved
    //private Set<User> usersWhoLoved;
    private Set<User> usersWhoLoved = new HashSet<>();
    
    // dùng để lưu - xóa yêu thích
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
        Template template = (Template) o;
        return id != null && id.equals(template.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}