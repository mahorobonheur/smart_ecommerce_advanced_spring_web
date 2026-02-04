package com.smart.ecommerce.dto.mapper;

import com.smart.ecommerce.dto.response.OrderItemResponseDTO;
import com.smart.ecommerce.model.OrderItem;

public class OrderItemMapper {
    public static OrderItemResponseDTO toDto(OrderItem orderItem){
        return new OrderItemResponseDTO (
                orderItem.getItemId(),
                orderItem.getOrder().getOrderId(),
        orderItem.getProduct().getProductId(),
        orderItem.getQuantity(),
        orderItem.getPrice());
    }
}
