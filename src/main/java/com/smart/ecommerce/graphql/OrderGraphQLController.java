package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.response.OrderItemResponseDTO;
import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderGraphQLController {

    @Autowired
    private OrderService orderService;

    @MutationMapping
    public OrderResponseDTO createOrder(@Argument OrderDTO input) {
        return toResponse(orderService.createOrder(input));
    }

    @QueryMapping
    public OrderResponseDTO orderById(@Argument String orderId) {
        return toResponse(orderService.getOrderById(UUID.fromString(orderId)));
    }

    @MutationMapping
    public OrderResponseDTO updateOrder(@Argument String orderId,
                                        @Argument OrderDTO input) {
        return toResponse(orderService.updateOrder(UUID.fromString(orderId), input));
    }

    @MutationMapping
    public Boolean deleteOrder(@Argument String orderId) {
        orderService.deleteOrder(UUID.fromString(orderId));
        return true;
    }

    private OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getItemId(),
                        order.getOrderId(),
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
                items
        );
    }
}
