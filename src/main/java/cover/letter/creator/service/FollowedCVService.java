package cover.letter.creator.service;

import cover.letter.creator.model.FollowedCV;
import cover.letter.creator.repository.FollowedCVRepository;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowedCVService {

    @Autowired
    private FollowedCVRepository followedCVRepository;

    @Autowired
    private UserRepository userRepository;

    public FollowedCV addFollowedCV(String email, FollowedCV followedCV) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        followedCV.setUserId(user.getId());
        return followedCVRepository.save(followedCV);
    }

    public List<FollowedCV> getFollowedCVsByUserEmail(String email) {
        return followedCVRepository.findByUserEmail(email);
    }

    public FollowedCV updateFollowedCV(Long id, String email, FollowedCV updatedCV) {
        Optional<FollowedCV> existingCV = followedCVRepository.findByIdAndUserEmail(id, email);
        if (existingCV.isPresent()) {
            FollowedCV cv = existingCV.get();
            if (updatedCV.getNote() != null) {
                cv.setNote(updatedCV.getNote());
            }
            if (updatedCV.getCompany() != null) {
                cv.setCompany(updatedCV.getCompany());
            }
            if (updatedCV.getStatus() != null) {
                cv.setStatus(updatedCV.getStatus());
            }
            return followedCVRepository.save(cv);
        } else {
            throw new RuntimeException("CV không tồn tại hoặc bạn không có quyền cập nhật");
        }
    }
    
    public void deleteFollowedCV(Long id, String email) {
        Optional<FollowedCV> existingCV = followedCVRepository.findByIdAndUserEmail(id, email);
        if (existingCV.isPresent()) {
            followedCVRepository.deleteById(id);
        } else {
            throw new RuntimeException("CV không tồn tại hoặc bạn không có quyền xóa");
        }
    }
}