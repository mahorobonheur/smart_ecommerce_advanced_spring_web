package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.OrderDTO;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Profile("dev")
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
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {
                    ResourceNotFoundException.class,
                    IllegalStateException.class
            }
    )
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
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ordersPage", allEntries = true)
    public Order confirmPaymentAndCreateOrder(UUID userId, String paymentIntentId)
            throws StripeException {

        Stripe.apiKey = stripeApiKey;

        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            Map<String, Object> confirmParams = new HashMap<>();
            confirmParams.put("payment_method", "pm_card_visa");
            confirmParams.put("return_url", "http://localhost:8080");
            intent = intent.confirm(confirmParams);
        }

        if (!"succeeded".equals(intent.getStatus())) {
            throw new IllegalStateException(
                    "Payment not completed. Current status: " + intent.getStatus()
            );
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

            ProductDTO productDTO = new ProductDTO(
                    product.getProductName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getCategory().getCategoryId()
            );

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
    @Transactional(readOnly = true)
    @Cacheable(value = "orderById", key = "#orderId")
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "ordersPage",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public Page<Order> allOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = IllegalStateException.class
    )
    @Caching(evict = {
            @CacheEvict(value = "ordersPage", allEntries = true),
            @CacheEvict(value = "orderById", key = "#orderId")
    })
    public Order updateOrder(UUID orderId, OrderDTO dto) {

        Order existingOrder = getOrderById(orderId);
        OrderStatus currentStatus = existingOrder.getStatus();

        Map<OrderStatus, Set<OrderStatus>> allowedTransitions = Map.of(
                OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
                OrderStatus.PAID, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
                OrderStatus.SHIPPED, Set.of(),
                OrderStatus.CANCELLED, Set.of()
        );

        Set<OrderStatus> allowedNext =
                allowedTransitions.getOrDefault(currentStatus, Set.of());

        OrderStatus nextStatus = OrderStatus.valueOf(dto.getStatus());

        if (!allowedNext.contains(nextStatus)) {
            throw new IllegalStateException(
                    "Invalid order state transition " + currentStatus + " to " + nextStatus
            );
        }

        existingOrder.setStatus(nextStatus);
        existingOrder.setOrderDate(LocalDateTime.now());

        return orderRepository.save(existingOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "ordersPage", allEntries = true),
            @CacheEvict(value = "orderById", key = "#orderId")
    })
    public void deleteOrder(UUID orderId) {

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        orderRepository.deleteById(orderId);
    }
}
