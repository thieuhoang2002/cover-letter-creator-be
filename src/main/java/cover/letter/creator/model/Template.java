package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

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
    
    @Column(name = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'active'")
    private String status = "active";
    
    @ManyToMany(mappedBy = "lovedTemplates")
    @JsonIgnore
    private Set<User> usersWhoLoved = new HashSet<>();
    
    @Transient
    private boolean isFavorite;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Set<User> getUsersWhoLoved() { return usersWhoLoved; }
    public void setUsersWhoLoved(Set<User> usersWhoLoved) { this.usersWhoLoved = usersWhoLoved; }

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