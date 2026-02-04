package com.smart.ecommerce.dto.mapper;

import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.model.Order;

public class OrderMapper {

    public static OrderResponseDTO toDto(Order order) {
        return new OrderResponseDTO(
                order.getOrderId(),
                order.getTotal(),
                order.getStatus().name(),
                order.getUser().getUserId(), // NOT whole user
                order.getOrderDate(),
                order.getItems()
                        .stream()
                        .map(OrderItemMapper::toDto)
                        .toList()
        );
    }
}

