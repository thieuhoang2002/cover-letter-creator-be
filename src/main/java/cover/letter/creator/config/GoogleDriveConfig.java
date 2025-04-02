package cover.letter.creator.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {
    private static final String APPLICATION_NAME = "Spring Boot Google Drive";
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static final String SERVICE_ACCOUNT_KEY_PATH = "/calendar-438415-5bdb470fb244.json";
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveConfig.class);
    private static final String FOLDER_NAME = "PDF FROM COVER LETTER CREATOR WEBSITE";
    private String folderId;

    @Bean
    public Drive driveService() throws IOException, GeneralSecurityException {
        logger.info("Initializing Google Drive service...");

        InputStream keyStream = GoogleDriveConfig.class.getResourceAsStream(SERVICE_ACCOUNT_KEY_PATH);
        if (keyStream == null) {
            logger.error("Service account key file not found at: {}", SERVICE_ACCOUNT_KEY_PATH);
            throw new IOException("Service account key file not found at: " + SERVICE_ACCOUNT_KEY_PATH);
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(keyStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        HttpCredentialsAdapter credentialsAdapter = new HttpCredentialsAdapter(credentials);

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentialsAdapter)
                .setApplicationName(APPLICATION_NAME)
                .build();

        folderId = createFolderIfNotExists(drive);
        logger.info("Folder ID: {}", folderId);

        // Chia sẻ thư mục với tài khoản cá nhân
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
//                .setEmailAddress("thhoang0903@gmail.com");
        drive.permissions().create(folderId, permission).execute();
        logger.info("Folder '{}' has been made public with 'Anyone with the link' access.", FOLDER_NAME);

        logger.info("Google Drive service initialized successfully.");
        return drive;
    }

    private String createFolderIfNotExists(Drive drive) throws IOException {
        logger.info("Searching for folder: {}", FOLDER_NAME);

        // Sửa câu truy vấn tìm kiếm thư mục
        Drive.Files.List request = drive.files().list()
                .setQ("name='" + FOLDER_NAME + "' and mimeType='application/vnd.google-apps.folder'")
                .setFields("files(id)")
                .setSpaces("drive");

        File folder = request.execute().getFiles().stream().findFirst().orElse(null);

        if (folder != null) {
            logger.info("Folder '{}' already exists with ID: {}", FOLDER_NAME, folder.getId());
            return folder.getId();
        }

        // Nếu không tìm thấy, tạo thư mục mới
        File fileMetadata = new File();
        fileMetadata.setName(FOLDER_NAME);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File createdFolder = drive.files().create(fileMetadata)
                .setFields("id")
                .execute();

        logger.info("Created new folder '{}' with ID: {}", FOLDER_NAME, createdFolder.getId());
        return createdFolder.getId();
    }


    public String getFolderId() {
        return folderId;
    }
}