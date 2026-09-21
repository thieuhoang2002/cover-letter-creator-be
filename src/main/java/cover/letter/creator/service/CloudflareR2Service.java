package cover.letter.creator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Service
public class CloudflareR2Service {

    private static final Logger logger = LoggerFactory.getLogger(CloudflareR2Service.class);

    @Value("${cloudflare.r2.account-id:}")
    private String accountId;

    @Value("${cloudflare.r2.access-key:}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key:}")
    private String secretKey;

    @Value("${cloudflare.r2.bucket-name:cover-letter-cv-storage}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url:}")
    private String publicUrl;

    /**
     * Khởi tạo S3Client kết nối tới Cloudflare R2
     */
    private S3Client getS3Client() {
        if (accountId == null || accountId.trim().isEmpty() ||
            accessKey == null || accessKey.trim().isEmpty() ||
            secretKey == null || secretKey.trim().isEmpty()) {
            logger.warn("Cloudflare R2 credentials are not fully configured.");
            return null;
        }

        // Endpoint chuẩn của Cloudflare R2: https://<account_id>.r2.cloudflarestorage.com
        String endpoint = "https://" + accountId.trim() + ".r2.cloudflarestorage.com";

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey.trim(), secretKey.trim());

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto")) // Cloudflare R2 sử dụng region "auto"
                .build();
    }

    /**
     * Upload mảng byte PDF lên Cloudflare R2
     * @param fileName Tên file (ví dụ: my_cv.pdf)
     * @param pdfBytes Dữ liệu nhị phân PDF
     * @return Public URL truy cập file hoặc null nếu chưa config
     */
    public String uploadPdf(String fileName, byte[] pdfBytes) {
        try {
            S3Client s3 = getS3Client();
            if (s3 == null) {
                logger.warn("Skipping upload to R2 because R2 credentials are not set.");
                return fileName;
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName.trim())
                    .key(fileName)
                    .contentType("application/pdf")
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromBytes(pdfBytes));
            logger.info("Successfully uploaded file '{}' to Cloudflare R2 bucket '{}'", fileName, bucketName);

            // Trả về Public URL nếu có cấu hình domain/dev URL
            if (publicUrl != null && !publicUrl.trim().isEmpty()) {
                String baseUrl = publicUrl.trim().replaceAll("/+$", "");
                return baseUrl + "/" + fileName;
            }

            return fileName;
        } catch (Exception e) {
            logger.error("Failed to upload file to Cloudflare R2: {}", e.getMessage(), e);
            return fileName;
        }
    }

    /**
     * Xóa file khỏi Cloudflare R2
     */
    public void deleteFile(String fileKeyOrUrl) {
        try {
            S3Client s3 = getS3Client();
            if (s3 == null || fileKeyOrUrl == null || fileKeyOrUrl.trim().isEmpty()) {
                return;
            }

            // Tách key từ URL nếu truyền vào là full URL
            String fileKey = fileKeyOrUrl;
            if (fileKey.contains("/")) {
                fileKey = fileKey.substring(fileKey.lastIndexOf("/") + 1);
            }

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName.trim())
                    .key(fileKey)
                    .build();

            s3.deleteObject(deleteRequest);
            logger.info("Deleted file '{}' from Cloudflare R2 bucket '{}'", fileKey, bucketName);
        } catch (Exception e) {
            logger.error("Failed to delete file from Cloudflare R2: {}", e.getMessage(), e);
        }
    }
}
