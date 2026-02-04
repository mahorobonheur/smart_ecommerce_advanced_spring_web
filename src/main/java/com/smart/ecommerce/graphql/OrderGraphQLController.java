package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.response.OrderItemResponseDTO;
import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.OrderItem;
import com.smart.ecommerce.model.OrderStatus;
import com.smart.ecommerce.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class OrderGraphQLController {

    @Autowired
    private OrderService orderService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO orderById(@Argument UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        return mapToOrderResponse(order);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<OrderResponseDTO> allOrders(
            @Argument int page,
            @Argument int size
    ) {
        Page<Order> orders =
                orderService.allOrders(PageRequest.of(page, size));

        return orders.getContent()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> checkout(@Argument UUID userId) throws StripeException {
        return orderService.checkout(userId);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO confirmPayment(
            @Argument UUID userId,
            @Argument String paymentIntentId
    ) throws StripeException {

        Order order = orderService.confirmPaymentAndCreateOrder(userId, paymentIntentId);
        return mapToOrderResponse(order);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO updateOrder(
            @Argument UUID orderId,
            @Argument OrderStatus status
            ) {
        OrderDTO input = new OrderDTO();
        input.setStatus(String.valueOf(status));
        Order updatedOrder = orderService.updateOrder(orderId, input);
        return mapToOrderResponse(updatedOrder);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteOrder(@Argument UUID orderId) {
        orderService.deleteOrder(orderId);
        return true;
    }

    private OrderResponseDTO mapToOrderResponse(Order order) {

        List<OrderItemResponseDTO> items = order.getItems()
                .stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getOrderId(),
                order.getTotal(),
                order.getStatus().name(),
                order.getUser().getUserId(),
                order.getOrderDate(),
                items
        );
    }

    private OrderItemResponseDTO mapToOrderItemResponse(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getItemId(),
                item.getOrder().getOrderId(),
                item.getProduct().getProductId(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
