package com.smart.ecommerce.service;

import com.smart.ecommerce.model.Cart;
import com.smart.ecommerce.model.User;

import java.util.UUID;

public interface CartService {

    Cart getCartByUser(User user);

    Cart addItemToCart(User user, UUID productId, int quantity);

    Cart removeItemFromCart(User user, UUID productId);

    void clearCart(User user);
}
