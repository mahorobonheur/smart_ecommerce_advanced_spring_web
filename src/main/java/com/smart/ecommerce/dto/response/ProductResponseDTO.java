package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductResponseDTO {
    private UUID productId;
    private String productName;
    private double price;
    private int stock;
    private UUID inventoryId;
    private UUID categoryId;
    private LocalDateTime createdAt;
}
