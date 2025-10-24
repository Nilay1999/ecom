package com.example.ecommerce.catalog.dto.image;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponseDto {
    private UUID id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
    private String altText;
    private String imageType;
    private String dimensions;
    private Long fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private Double aspectRatio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

