package cover.letter.creator.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "educations")
@Data
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String school;

    @Column
    private String degree; // E.g., "Bachelor of Science"

    @Column
    private String fieldOfStudy; // E.g., "Computer Science"

    @Column
    private String time; // E.g., "2018 - 2022"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Dùng JsonIgnore thay cho JsonBackReferences
    private User user;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Education that = (Education) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}