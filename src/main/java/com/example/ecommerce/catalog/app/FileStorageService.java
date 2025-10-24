package com.example.ecommerce.catalog.app;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String directory);

    void deleteFile(String fileUrl);
}
