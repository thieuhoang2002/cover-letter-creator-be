package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    
    private String school;
    
    //chuyên ngành
    private String specialization;

    // Mối quan hệ One-to-Many với CoverLetterPdf
    @OneToMany(mappedBy = "user")
    private List<CoverLetterPdf> coverLetters;

    // Mối quan hệ Many-to-Many với Template
//    @ManyToMany()
    @ManyToMany
    @JoinTable(
        name = "user_loved_templates",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "template_id")
    )
    //@JsonIgnore // Ngăn Jackson serialize lovedTemplates
//    private Set<Template> lovedTemplates;
    private Set<Template> lovedTemplates = new HashSet<>();
    

}