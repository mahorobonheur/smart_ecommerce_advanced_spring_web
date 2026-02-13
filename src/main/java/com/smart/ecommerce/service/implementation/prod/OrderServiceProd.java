package com.smart.ecommerce.service.implementation.prod;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.request.OrderItemDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.*;
import com.smart.ecommerce.repository.CartRepository;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.OrderItemService;
import com.smart.ecommerce.service.OrderService;
import com.smart.ecommerce.service.ProductService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Profile("prod")
public class OrderServiceProd implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartRepository cartRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private Executor asyncExecutor;

    public CompletableFuture<Map<String, Object>> checkoutAsync(UUID userId){
        return CompletableFuture.supplyAsync(() ->
                {
                    try{
                        return checkout(userId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, asyncExecutor
        );
    }


    @Override
    @Transactional
    public Map<String, Object> checkout(UUID userId) throws StripeException {

        Stripe.apiKey = stripeApiKey;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        long totalAmount = 0;
        for (CartItem item : cart.getItems()) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("amount", totalAmount);
        params.put("currency", "rwf");
        params.put("payment_method_types", List.of("card"));

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return Map.of(
                "clientSecret", paymentIntent.getClientSecret(),
                "amount", totalAmount
        );
    }

    @Transactional
    @Override
    public Order confirmPaymentAndCreateOrder(UUID userId, String paymentIntentId) throws StripeException {

        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            throw new IllegalStateException("Payment not completed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        order.setPaymentIntentId(paymentIntentId);

        double total = 0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());

            order.getItems().add(item);
            total += item.getPrice() * item.getQuantity();
        }

        order.setTotal(total);

        cart.getItems().clear();

        return orderRepository.save(order);
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

        if (existingOrder.getItems() == null) {
            existingOrder.setItems(new java.util.ArrayList<>());
        } else {
            existingOrder.getItems().clear();
        }

        double total = 0;

        if (dto.getItems() != null) {
            for (OrderItemDTO itemDTO : dto.getItems()) {
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
