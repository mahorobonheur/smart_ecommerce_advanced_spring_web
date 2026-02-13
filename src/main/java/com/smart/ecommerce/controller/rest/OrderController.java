package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.response.OrderItemResponseDTO;
import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.service.OrderService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PostMapping("/checkout/{userId}")
    @Operation(summary = "Checkout",
    description = "Here only customers who are authenticated can access it to checkout their orders")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> checkout(
            @PathVariable UUID userId
    ) throws StripeException {

        return orderService.checkoutAsync(userId)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/confirm")
    @Operation(summary = "Here is for confirming payment",
    description = "Confirm payment, here you will use your paymentIntentId and then return result if payment is successful")
    public ResponseEntity<Order> confirmPayment(
            @RequestParam UUID userId,
            @RequestParam String paymentIntentId
    ) throws StripeException {

        Order order = orderService.confirmPaymentAndCreateOrder(userId, paymentIntentId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("{orderId}")
    @Operation(summary = "Get order by Id",
    description = "This is for getting order by it's ID, and it requires authentication")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId){
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(toResponse(order));
    }

    @GetMapping
    @Operation(summary = "Get all orders",
    description = "Here is for getting all orders, and it is only for Admins")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(Pageable pageable){
        Page<OrderResponseDTO> orders = orderService.allOrders(pageable).map(this::toResponse);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("{orderId}")
    @Operation(summary = "Update order",
    description = "Update order api to be used for updating orders, it can only be accessed for authenticated users")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable UUID orderId, @RequestParam String status){
        OrderDTO dto = new OrderDTO();
        dto.setStatus(status);
        Order order = orderService.updateOrder(orderId, dto);
        return ResponseEntity.ok(toResponse(order));
    }

    @DeleteMapping("{orderId}")
    @Operation(summary = "Delete order",
    description = "Delete order only accessible by authorized users")
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
