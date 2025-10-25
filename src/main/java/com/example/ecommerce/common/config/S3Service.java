package com.example.ecommerce.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsProperties awsProperties;

    // ==================== PRODUCT SPECIFIC METHODS ====================

    /**
     * Upload product image to S3
     */
    public String uploadProductImage(MultipartFile file, UUID productId) throws IOException {
        String fileKey = generateProductImageKey(file.getOriginalFilename(), productId);
        return uploadFileToS3(file, fileKey);
    }

    /**
     * Generate presigned URL for product image upload
     * 
     * @throws IOException
     */
    public Map<String, String> generateProductImageUploadUrl(MultipartFile file, UUID productId) throws IOException {
        String fileKey = generateProductImageKey(file.getOriginalFilename(), productId);
        String folderKey = uploadFileToS3(file, fileKey);

        String contentType = getContentTypeFromFileName(folderKey);
        String presignedUrl = generatePresignedUploadUrl(fileKey, contentType);
        String publicUrl = getPublicUrl(fileKey);

        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("fileKey", fileKey);
        response.put("publicUrl", publicUrl);
        response.put("message", "Presigned URL generated successfully for product image");

        return response;
    }

    /**
     * Upload multiple product images
     */
    public List<String> uploadProductImages(List<MultipartFile> files, UUID productId) throws IOException {
        List<String> uploadedFileKeys = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = uploadProductImage(file, productId);
            uploadedFileKeys.add(fileKey);
        }

        return uploadedFileKeys;
    }

    /**
     * Delete product image from S3
     */
    public void deleteProductImage(String fileKey) {
        deleteFile(fileKey);
        log.info("Product image deleted from S3: {}", fileKey);
    }

    /**
     * Delete all images for a product
     */
    public void deleteAllProductImages(Long productId) {
        try {
            // List all objects in the product folder
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(awsProperties.getS3().getBucketName())
                    .prefix(awsProperties.getS3().getProductsFolder() + "/" + productId + "/")
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            // Delete all objects
            for (S3Object s3Object : listResponse.contents()) {
                deleteFile(s3Object.key());
                log.info("Deleted product image: {}", s3Object.key());
            }

        } catch (S3Exception e) {
            log.error("Error deleting product images: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Failed to delete product images", e);
        }
    }

    // ==================== GENERAL METHODS ====================

    /**
     * Upload file to S3 with specific key
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileKey = generateFileKey(file.getOriginalFilename(), folder);
        return uploadFileToS3(file, fileKey);
    }

    private String uploadFileToS3(MultipartFile file, String fileKey) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(fileKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        log.info("File uploaded successfully to S3: {}", fileKey);
        return fileKey;
    }

    /**
     * Generate presigned URL for file upload
     */
    public String generatePresignedUploadUrl(String fileKey, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(b -> b
                .signatureDuration(Duration.ofSeconds(awsProperties.getS3().getPresignedUrlExpiration()))
                .putObjectRequest(putObjectRequest));

        return presignedRequest.url().toString();
    }

    /**
     * Generate presigned URL for file download
     */
    public String generatePresignedDownloadUrl(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(fileKey)
                .build();

        return s3Presigner.presignGetObject(b -> b
                .signatureDuration(Duration.ofSeconds(awsProperties.getS3().getPresignedUrlExpiration()))
                .getObjectRequest(getObjectRequest))
                .url().toString();
    }

    /**
     * Delete file from S3
     */
    public void deleteFile(String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("File deleted from S3: {}", fileKey);
    }

    /**
     * Check if file exists in S3
     */
    public boolean fileExists(String fileKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucketName())
                    .key(fileKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking file existence in S3: {}", e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    // ==================== KEY GENERATION METHODS ====================

    /**
     * Generate product image key with folder structure:
     * products/{productId}/{uuid}.{ext}
     */
    private String generateProductImageKey(String originalFileName, UUID productId) {
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID() + fileExtension;

        return awsProperties.getS3().getProductsFolder() + "/" + productId + "/" + fileName;
    }

    /**
     * Generate general file key with folder
     */
    private String generateFileKey(String originalFileName, String folder) {
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID() + fileExtension;

        if (folder != null && !folder.trim().isEmpty()) {
            return folder + "/" + fileName;
        }

        return fileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getContentTypeFromFileName(String fileName) {
        if (fileName == null) {
            return "application/octet-stream"; // default binary type
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "bmp":
                return "image/bmp";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "json":
                return "application/json";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * Get public URL for file
     */
    public String getPublicUrl(String fileKey) {
        if (awsProperties.getS3().getCloudFrontDomain() != null &&
                !awsProperties.getS3().getCloudFrontDomain().isEmpty()) {
            return "https://" + awsProperties.getS3().getCloudFrontDomain() + "/" + fileKey;
        }

        return "https://" + awsProperties.getS3().getBucketName() +
                ".s3." + awsProperties.getRegion() + ".amazonaws.com/" + fileKey;
    }
}
