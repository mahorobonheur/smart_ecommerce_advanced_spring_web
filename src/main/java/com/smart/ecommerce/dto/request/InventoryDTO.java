package com.smart.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InventoryDTO {

    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    @Min(value = 0, message = "Quantity must be at least 0")
    private int quantityAvailable;
}
