package cover.letter.creator.repository;

import cover.letter.creator.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
//	Optional<User> findByEmail(String email);
	
	@EntityGraph(attributePaths = {"lovedTemplates", "lovedTemplatesModern"})
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.lovedTemplates LEFT JOIN FETCH u.lovedTemplatesModern WHERE u.email = :email")
	Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"lovedTemplates", "lovedTemplatesModern"})
    Optional<User> findById(Integer id);
    
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.skills " +
           "LEFT JOIN FETCH u.experiences " +
           "LEFT JOIN FETCH u.educations " +
           "LEFT JOIN FETCH u.certificates " +
           "LEFT JOIN FETCH u.hobbies " +
           "LEFT JOIN FETCH u.lovedTemplates " +
           "LEFT JOIN FETCH u.lovedTemplatesModern " +
           "WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(@Param("email") String email);
}