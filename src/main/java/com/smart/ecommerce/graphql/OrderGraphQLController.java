package com.smart.ecommerce.graphql;

import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
public class OrderGraphQLController {

    @Autowired
    private OrderService orderService;

    @QueryMapping
    public Order orderById(@Argument UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @MutationMapping
    public Map<String, Object> checkout(@Argument UUID userId) throws StripeException {
        return orderService.checkout(userId);
    }

    @MutationMapping
    public Order confirmPayment(
            @Argument UUID userId,
            @Argument String paymentIntentId
    ) throws StripeException {
        return orderService.confirmPaymentAndCreateOrder(userId, paymentIntentId);
    }
}
