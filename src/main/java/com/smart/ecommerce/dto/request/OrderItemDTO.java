package com.smart.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderItemDTO {
    private UUID productId;
    private Integer quantity;
    private Double price;
}
