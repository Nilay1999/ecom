package com.example.ecommerce.catalog.dto.image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageOrderRequestDto {

    @NotEmpty(message = "Image orders cannot be empty")
    @Valid
    private List<ImageOrder> imageOrders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageOrder {
        @NotNull(message = "Image ID is required")
        private UUID imageId;

        @NotNull(message = "Display order is required")
        private Integer displayOrder;
    }
}
