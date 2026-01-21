package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InventoryResponseDTO {
    private UUID inventoryId;
    private UUID productId;
    private String productName;
    private int quantityAvailable;
    private LocalDateTime lastUpdated;
}
