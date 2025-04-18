package cover.letter.creator.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import cover.letter.creator.model.PasswordResetToken;
import cover.letter.creator.model.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);

}
