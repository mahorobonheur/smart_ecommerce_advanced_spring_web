package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Cart;
import com.smart.ecommerce.model.CartItem;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.CartRepository;
import com.smart.ecommerce.service.CartService;
import com.smart.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CartServiceDevImplementation implements CartService {

    private final CartRepository cartRepository;
    @Autowired
    private ProductService productService;

    public CartServiceDevImplementation(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart addItemToCart(User user, UUID productId, int quantity) {

        Cart cart = getCartByUser(user);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(
                    existingItem.get().getQuantity() + quantity
            );
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setQuantity(quantity);
            item.setProduct(productService.getProductById(productId));
            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(User user, UUID productId) {

        Cart cart = getCartByUser(user);

        cart.getItems().removeIf(
                item -> item.getProduct().getProductId().equals(productId)
        );

        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getCartByUser(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}

