package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    Order createOrder(OrderDTO dto);
    Order getOrderById(UUID orderId);
    Page<Order> allOrders(Pageable pageable);
    Order updateOrder(UUID orderId, OrderDTO dto);
    void deleteOrder(UUID orderId);
}
