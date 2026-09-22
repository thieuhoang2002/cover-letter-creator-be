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
     * @return Public URL truy cập file hoặc fileName nếu chưa config
     */
    public String uploadPdf(String fileName, byte[] pdfBytes) {
        return uploadFile(fileName, pdfBytes, "application/pdf");
    }

    /**
     * Upload bất kỳ loại file nào lên Cloudflare R2
     * @param fileName Key trong bucket (ví dụ: avatars/uuid.jpg, customer-cvs/uuid.pdf)
     * @param fileBytes Dữ liệu nhị phân
     * @param contentType MIME type (image/jpeg, image/png, application/pdf, ...)
     * @return Public URL hoặc fileName nếu chưa config
     */
    public String uploadFile(String fileName, byte[] fileBytes, String contentType) {
        try {
            S3Client s3 = getS3Client();
            if (s3 == null) {
                logger.warn("Skipping upload to R2 because R2 credentials are not set.");
                return fileName;
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName.trim())
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
            logger.info("Successfully uploaded '{}' ({}) to Cloudflare R2 bucket '{}'", fileName, contentType, bucketName);

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
     * @param fileKeyOrUrl Key trong bucket (ví dụ: customer-cvs/1/uuid.pdf) hoặc Full URL
     */
    public void deleteFile(String fileKeyOrUrl) {
        try {
            S3Client s3 = getS3Client();
            if (s3 == null || fileKeyOrUrl == null || fileKeyOrUrl.trim().isEmpty()) {
                return;
            }

            String fileKey = fileKeyOrUrl.trim();

            // Nếu truyền vào full public URL, bóc tách phần path phía sau domain
            if (publicUrl != null && !publicUrl.trim().isEmpty()) {
                String baseUrl = publicUrl.trim().replaceAll("/+$", "");
                if (fileKey.startsWith(baseUrl)) {
                    fileKey = fileKey.substring(baseUrl.length()).replaceAll("^/+", "");
                }
            }

            // Nếu vẫn là full URL (http:// hoặc https://)
            if (fileKey.startsWith("http://") || fileKey.startsWith("https://")) {
                try {
                    java.net.URI uri = java.net.URI.create(fileKey);
                    fileKey = uri.getPath().replaceAll("^/+", "");
                } catch (Exception ignored) {}
            }

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName.trim())
                    .key(fileKey)
                    .build();

            s3.deleteObject(deleteRequest);
            logger.info("Successfully requested deletion of key '{}' from Cloudflare R2 bucket '{}'", fileKey, bucketName);
        } catch (Exception e) {
            logger.error("Failed to delete file from Cloudflare R2: {}", e.getMessage(), e);
        }
    }
}
