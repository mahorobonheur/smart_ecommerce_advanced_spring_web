package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.OrderItemDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.OrderItem;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.repository.OrderItemRepository;
import com.smart.ecommerce.repository.ProductRepository;
import com.smart.ecommerce.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Profile("dev")
@Transactional
public class OrderItemServiceDevImplementation implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public OrderItem addOrderItem(OrderItemDTO dto, Order order) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(dto.getPrice());

        order.getItems().add(orderItem);

        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem getOrderItemById(UUID itemId) {
        return orderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));
    }

    @Override
    public Page<OrderItem> allOrderItems(Pageable pageable) {
        return orderItemRepository.findAll(pageable);
    }

    @Override
    public OrderItem updateOrderItem(UUID itemId, OrderItemDTO dto) {
        OrderItem existingItem = getOrderItemById(itemId);
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        existingItem.setProduct(product);
        existingItem.setQuantity(dto.getQuantity());
        existingItem.setPrice(dto.getPrice());

        return orderItemRepository.save(existingItem);
    }

    @Override
    public void deleteOrderItem(UUID itemId) {
        if (!orderItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Order item not found");
        }
        orderItemRepository.deleteById(itemId);
    }
}
