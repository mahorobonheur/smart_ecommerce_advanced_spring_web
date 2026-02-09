package com.smart.ecommerce.graphql;


import com.smart.ecommerce.dto.response.CartResponseDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class CartGraphQLController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public CartResponseDTO cartByUser(@Argument UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return CartResponseDTO.fromCart(cartService.getCartByUser(user));
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    public CartResponseDTO removeItemFromCart(
            @Argument UUID userId,
            @Argument UUID productId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return CartResponseDTO.fromCart(cartService.removeItemFromCart(user, productId));
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean clearCart(@Argument UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        cartService.clearCart(user);
        return true;
    }
}
