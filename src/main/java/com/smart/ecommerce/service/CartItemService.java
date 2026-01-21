package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.CartItemDTO;
import com.smart.ecommerce.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CartItemService {
    CartItem createCartItem(CartItemDTO dto);
    CartItem getCartById(UUID cartId);
    Page<CartItem> allCarts(Pageable pageable);
    CartItem updateCart(UUID cartId, CartItemDTO dto);
    void deleteCart(UUID cartId);
}
