package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    
    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    private Date birthday;
    private String address;
    private String phone;
    private String specialization;

    // Cover letters
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<CoverLetterPdf> coverLetters;

    // Loved templates
    @ManyToMany
    @JoinTable(
        name = "user_loved_templates",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "template_id")
    )
    @JsonIgnore
    private Set<Template> lovedTemplates = new HashSet<>();

    // Loved modern templates
    @ManyToMany
    @JoinTable(
        name = "user_loved_modern_templates",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "modern_template_id")
    )
    @JsonIgnore
    private Set<TemplateModernCV> lovedTemplatesModern = new HashSet<>();

    // Skills
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Skill> skills = new HashSet<>();

    // Experiences
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Experience> experiences = new HashSet<>();

    // Educations
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Education> educations = new HashSet<>();

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Certificate> certificates = new HashSet<>();


    // Hobbies
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Hobby> hobbies = new HashSet<>();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
