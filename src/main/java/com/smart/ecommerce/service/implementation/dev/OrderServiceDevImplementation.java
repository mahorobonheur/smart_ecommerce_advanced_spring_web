package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.request.OrderItemDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.OrderItem;
import com.smart.ecommerce.model.OrderStatus;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.OrderService;
import com.smart.ecommerce.service.OrderItemService;
import com.smart.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        // Save order first
        Order savedOrder = orderRepository.save(order);

        double total = 0;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            OrderItem orderItem = orderItemService.addOrderItem(itemDTO, savedOrder);
            total += orderItem.getPrice() * orderItem.getQuantity();
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

        double total = 0;
        List<OrderItem> updatedItems = new ArrayList<>();

        if (dto.getItems() != null) {
            for (OrderItemDTO itemDTO : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrder(existingOrder);
                item.setProduct(productService.getProductById(itemDTO.getProductId()));
                item.setQuantity(itemDTO.getQuantity());
                item.setPrice(itemDTO.getPrice());

                updatedItems.add(item);
                total += item.getPrice() * item.getQuantity();
            }
        }

        existingOrder.getItems().clear();
        existingOrder.getItems().addAll(updatedItems);

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
