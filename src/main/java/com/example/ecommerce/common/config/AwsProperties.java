package com.example.ecommerce.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String accessKeyId;
    private String secretAccessKey;
    private String region;

    private S3 s3 = new S3();

    @Data
    public static class S3 {
        private String bucketName;
        private String endpoint;
        private Long presignedUrlExpiration = 3600L;
        private String cloudFrontDomain;
        private String productsFolder = "products";
    }
}
