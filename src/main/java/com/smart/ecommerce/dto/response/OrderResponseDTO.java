package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private UUID orderId;
    private double total;
    private String status;
    private UUID userId;
    private LocalDateTime orderDate;
    private List<OrderItemResponseDTO> items;
}
