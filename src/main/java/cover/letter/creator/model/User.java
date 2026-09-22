package cover.letter.creator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    // Explicit Getters and Setters to guarantee compiler resolution
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public List<CoverLetterPdf> getCoverLetters() { return coverLetters; }
    public void setCoverLetters(List<CoverLetterPdf> coverLetters) { this.coverLetters = coverLetters; }

    public Set<Template> getLovedTemplates() { return lovedTemplates; }
    public void setLovedTemplates(Set<Template> lovedTemplates) { this.lovedTemplates = lovedTemplates; }

    public Set<TemplateModernCV> getLovedTemplatesModern() { return lovedTemplatesModern; }
    public void setLovedTemplatesModern(Set<TemplateModernCV> lovedTemplatesModern) { this.lovedTemplatesModern = lovedTemplatesModern; }

    public Set<Skill> getSkills() { return skills; }
    public void setSkills(Set<Skill> skills) { this.skills = skills; }

    public Set<Experience> getExperiences() { return experiences; }
    public void setExperiences(Set<Experience> experiences) { this.experiences = experiences; }

    public Set<Education> getEducations() { return educations; }
    public void setEducations(Set<Education> educations) { this.educations = educations; }

    public Set<Certificate> getCertificates() { return certificates; }
    public void setCertificates(Set<Certificate> certificates) { this.certificates = certificates; }

    public Set<Hobby> getHobbies() { return hobbies; }
    public void setHobbies(Set<Hobby> hobbies) { this.hobbies = hobbies; }
    
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
