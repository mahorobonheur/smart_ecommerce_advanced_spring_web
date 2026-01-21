package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.CartItemDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.CartItem;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.repository.CartRepository;
import com.smart.ecommerce.repository.ProductRepository;
import com.smart.ecommerce.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("dev")
public class CartItemServiceDevImplementation implements CartItemService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;

    @Override
    public CartItem createCartItem(CartItemDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product does not exist!"));
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(dto.getQuantity());
        return cartRepository.save(cartItem);
    }

    @Override
    public CartItem getCartById(UUID cartId) {
        return cartRepository.findById(cartId).orElseThrow(
                ()-> new ResourceNotFoundException("Cart not found!")
                );
    }

    @Override
    public Page<CartItem> allCarts(Pageable pageable) {
        return cartRepository.findAll(pageable);
    }

    @Override
    public CartItem updateCart(UUID cartId, CartItemDTO dto) {
        CartItem cartItem = getCartById(cartId);
        cartItem.setQuantity(dto.getQuantity());
        return cartRepository.save(cartItem);
    }

    @Override
    public void deleteCart(UUID cartId) {
        productRepository.deleteById(cartId);
    }
}
