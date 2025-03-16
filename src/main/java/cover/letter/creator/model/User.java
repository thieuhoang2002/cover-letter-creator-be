package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    private Date birthday;

    private String address;

    private String phone;

    // Mối quan hệ One-to-Many với CoverLetterPdf
    @OneToMany(mappedBy = "user")
    private List<CoverLetterPdf> coverLetters;

    // Mối quan hệ Many-to-Many với Template
    @ManyToMany
    @JoinTable(
        name = "user_loved_templates",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "template_id")
    )
    private Set<Template> lovedTemplates;
}