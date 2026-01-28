package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private UUID cartItemId;
    private UUID productId;
    private int quantity;
}
