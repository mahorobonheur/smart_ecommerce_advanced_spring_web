package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.OrderItemDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderItemService {
    OrderItem addOrderItem(OrderItemDTO dto, Order order);
    OrderItem getOrderItemById(UUID itemId);
    Page<OrderItem> allOrderItems(Pageable pageable);
    OrderItem updateOrderItem(UUID itemId, OrderItemDTO dto);
    void deleteOrderItem(UUID itemId);
}
