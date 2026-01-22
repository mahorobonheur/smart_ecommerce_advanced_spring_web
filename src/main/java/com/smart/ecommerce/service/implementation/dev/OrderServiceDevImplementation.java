package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.request.OrderItemDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.OrderItem;
import com.smart.ecommerce.model.OrderStatus;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.OrderItemService;
import com.smart.ecommerce.service.ProductService;
import com.smart.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Profile("dev")
@Transactional
public class OrderServiceDevImplementation implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Override
    public Order createOrder(OrderDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.valueOf(dto.getStatus()));
        order.setTotal(0.0);
        order.setItems(new java.util.ArrayList<>()); // Ensure list is initialized

        // Save order first to get generated ID
        Order savedOrder = orderRepository.save(order);

        double total = 0;

        // Add order items using service
        if (dto.getItems() != null) {
            for (OrderItemDTO itemDTO : dto.getItems()) {
                OrderItem orderItem = orderItemService.addOrderItem(itemDTO, savedOrder);
                total += orderItem.getPrice() * orderItem.getQuantity();
            }
        }

        savedOrder.setTotal(total);

        return orderRepository.save(savedOrder);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public Page<Order> allOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order updateOrder(UUID orderId, OrderDTO dto) {
        Order existingOrder = getOrderById(orderId);
        existingOrder.setStatus(OrderStatus.valueOf(dto.getStatus()));
        existingOrder.setOrderDate(LocalDateTime.now());

        // Ensure items list is initialized
        if (existingOrder.getItems() == null) {
            existingOrder.setItems(new java.util.ArrayList<>());
        } else {
            existingOrder.getItems().clear();
        }

        double total = 0;

        // Add or update order items
        if (dto.getItems() != null) {
            for (OrderItemDTO itemDTO : dto.getItems()) {
                // Use OrderItemService to handle proper associations
                OrderItem orderItem = orderItemService.addOrderItem(itemDTO, existingOrder);
                total += orderItem.getPrice() * orderItem.getQuantity();
            }
        }

        existingOrder.setTotal(total);

        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found");
        }
        orderRepository.deleteById(orderId);
    }
}
