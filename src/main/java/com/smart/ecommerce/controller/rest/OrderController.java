package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.response.OrderItemResponseDTO;
import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderDTO orderDTO){
        Order order = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(toResponse(order));
    }

    @GetMapping("{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId){
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(toResponse(order));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(Pageable pageable){
        Page<OrderResponseDTO> orders = orderService.allOrders(pageable).map(this::toResponse);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable UUID orderId, @RequestBody OrderDTO dto){
        Order order = orderService.updateOrder(orderId, dto);
        return ResponseEntity.ok(toResponse(order));
    }

    @DeleteMapping("{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }


    public OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getItemId(),
                        item.getOrder().getOrderId(),
                        item.getProduct().getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getOrderId(),
                order.getTotal(),
                order.getStatus().name(),
                order.getUser().getUserId(),
                order.getOrderDate(),
                itemDTOs
        );
    }

}
