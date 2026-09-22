package cover.letter.creator.repository;

import cover.letter.creator.model.VipUpgradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VipUpgradeRequestRepository extends JpaRepository<VipUpgradeRequest, Long> {

    List<VipUpgradeRequest> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<VipUpgradeRequest> findAllByOrderByCreatedAtDesc();

    Optional<VipUpgradeRequest> findTopByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, String status);

    long countByUserIdAndStatus(Integer userId, String status);
}
