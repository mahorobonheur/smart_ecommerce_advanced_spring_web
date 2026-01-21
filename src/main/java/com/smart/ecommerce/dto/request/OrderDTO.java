package com.smart.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderDTO {
    private UUID userId;
    private Double total;
    private String status;
    private List<OrderItemDTO> items;
}
