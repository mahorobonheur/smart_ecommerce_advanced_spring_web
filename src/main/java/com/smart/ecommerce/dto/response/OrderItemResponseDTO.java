package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {
    private UUID itemId;
    private UUID orderId;
    private UUID productId;
    private int quantity;
    private double price;
}
