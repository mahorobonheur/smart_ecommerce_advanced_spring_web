package com.smart.ecommerce.graphql;


import com.smart.ecommerce.dto.response.CartResponseDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class CartGraphQLController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @QueryMapping
    @Operation(summary = "GraphL: Get cart by user")
    public CartResponseDTO cartByUser(@Argument UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return CartResponseDTO.fromCart(cartService.getCartByUser(user));
    }

    @MutationMapping
    @Operation(summary = "GraphL: Add item to cart")
    public CartResponseDTO addItemToCart(
            @Argument UUID userId,
            @Argument UUID productId,
            @Argument int quantity
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return CartResponseDTO.fromCart(cartService.addItemToCart(user, productId, quantity));
    }

    @MutationMapping
    @Operation(summary = "GraphL: Remove item from cart")
    public CartResponseDTO removeItemFromCart(
            @Argument UUID userId,
            @Argument UUID productId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return CartResponseDTO.fromCart(cartService.removeItemFromCart(user, productId));
    }

    @MutationMapping
    @Operation(summary = "GraphL: Clear cart")
    public Boolean clearCart(@Argument UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        cartService.clearCart(user);
        return true;
    }
}
