package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Cart;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    @Operation(summary = "Get cart by Id",
            description = "Here is to get cart by user Id. This is secured, requires authentication")
    public ResponseEntity<Cart> getCart(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(cartService.getCartByUser(user));
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart",
    description = "Add item to cart requires authentication")
    public ResponseEntity<Cart> addItemToCart(
            @RequestParam UUID userId,
            @RequestParam UUID productId,
            @RequestParam int quantity
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartService.addItemToCart(user, productId, quantity);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/remove")
    @Operation(summary = "Remove item from cart",
    description = "Here is to remove an item from cart. It requires authentication")
    public ResponseEntity<Cart> removeItemFromCart(
            @RequestParam UUID userId,
            @RequestParam UUID productId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartService.removeItemFromCart(user, productId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear/{userId}")
    @Operation(summary = "Clear cart",
    description = "Clear cart by user Id, and then this also requires authentication")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
