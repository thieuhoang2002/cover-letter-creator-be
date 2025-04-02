package cover.letter.creator.controller;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import cover.letter.creator.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/drive")
public class GoogleDriveController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @Autowired
    private Drive driveService; // Tiêm Drive instance từ GoogleDriveConfig

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Upload file lên Google Drive
            String fileId = googleDriveService.uploadFile(file);

            // Chia sẻ file với tài khoản cá nhân
            Permission permission = new Permission()
                    .setType("user")
                    .setRole("writer") // Quyền ghi, có thể đổi thành "reader" nếu chỉ cần xem
                    .setEmailAddress("thhoang0903@gmail.com"); // Email cá nhân của bạn
            driveService.permissions().create(fileId, permission).execute();

            // Trả về phản hồi thành công với File ID
            return ResponseEntity.ok("File uploaded successfully to my Drive. File ID: " + fileId);
        } catch (IOException e) {
            // Trả về lỗi nếu có vấn đề trong quá trình upload hoặc chia sẻ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }
}