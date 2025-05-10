package cover.letter.creator.repository;

import cover.letter.creator.model.FollowedCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowedCVRepository extends JpaRepository<FollowedCV, Long> {
    @Query("SELECT f FROM FollowedCV f JOIN User u ON f.userId = u.id WHERE u.email = :email")
    List<FollowedCV> findByUserEmail(String email);

    @Query("SELECT f FROM FollowedCV f JOIN User u ON f.userId = u.id WHERE f.id = :id AND u.email = :email")
    Optional<FollowedCV> findByIdAndUserEmail(Long id, String email);
}