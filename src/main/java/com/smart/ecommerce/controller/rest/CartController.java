package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Cart;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.CartService;
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
    public ResponseEntity<Cart> getCart(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(cartService.getCartByUser(user));
    }

    @PostMapping("/add")
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
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
