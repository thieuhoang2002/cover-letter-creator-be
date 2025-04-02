package cover.letter.creator.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cover.letter.creator.config.GoogleDriveConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleDriveService {
    private final Drive driveService;
    private final GoogleDriveConfig googleDriveConfig;

    @Autowired
    public GoogleDriveService(Drive driveService, GoogleDriveConfig googleDriveConfig) {
        this.driveService = driveService;
        this.googleDriveConfig = googleDriveConfig;
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());

        // Set parent folder là thư mục "PDF FROM COVER LETTER CREATOR WEBSITE"
        String folderId = googleDriveConfig.getFolderId();
        fileMetadata.setParents(Collections.singletonList(folderId));

        java.io.File tempFile = convertMultiPartToFile(multipartFile);
        FileContent mediaContent = new FileContent(multipartFile.getContentType(), tempFile);

        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        tempFile.delete();
        return file.getId();
    }

    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
        java.io.File convFile = new java.io.File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}