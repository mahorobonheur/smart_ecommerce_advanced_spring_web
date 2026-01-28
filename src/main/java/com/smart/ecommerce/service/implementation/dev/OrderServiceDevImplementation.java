package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.OrderDTO;
import com.smart.ecommerce.dto.request.OrderItemDTO;
import com.smart.ecommerce.dto.request.ProductDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.*;
import com.smart.ecommerce.repository.CartRepository;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.OrderItemService;
import com.smart.ecommerce.service.ProductService;
import com.smart.ecommerce.service.OrderService;
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
import java.util.*;

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

    @Autowired
    private CartRepository cartRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

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

            Product product = item.getProduct();


            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product: " + product.getProductName()
                );
            }

            totalAmount += product.getPrice() * item.getQuantity();
        }

        if (totalAmount <= 0) {
            throw new IllegalStateException("Invalid cart total");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("amount", totalAmount);
        params.put("currency", "rwf");
        params.put("payment_method_types", List.of("card"));

        PaymentIntent intent = PaymentIntent.create(params);

        return Map.of(
                "clientSecret", intent.getClientSecret(),
                "paymentIntentId", intent.getId(),
                "amount", totalAmount
        );
    }



    @Override
    @Transactional
    public Order confirmPaymentAndCreateOrder(UUID userId, String paymentIntentId)
            throws StripeException {

        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            throw new IllegalStateException("Payment not completed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        order.setPaymentIntentId(paymentIntentId);

        double total = 0;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Stock changed. Not enough stock for: " + product.getProductName()
                );
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            ProductDTO productDTO = new ProductDTO(product.getProductName(), product.getPrice(), product.getStock(), product.getCategory().getCategoryId());
            productService.updateProduct(product.getProductId(), productDTO);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setPrice(product.getPrice());

            order.getItems().add(orderItem);
            total += orderItem.getPrice() * orderItem.getQuantity();
        }

        order.setTotal(total);
        cart.getItems().clear();
        cartRepository.save(cart);

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
