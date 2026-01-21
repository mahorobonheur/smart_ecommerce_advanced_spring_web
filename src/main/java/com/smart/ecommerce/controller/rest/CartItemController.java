package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.dto.request.CartItemDTO;
import com.smart.ecommerce.dto.response.CartItemResponse;
import com.smart.ecommerce.model.CartItem;
import com.smart.ecommerce.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @PostMapping
    @Operation(summary = "Create cart item")
    public ResponseEntity<CartItemResponse> createCartItem(@RequestBody CartItemDTO dto){
        CartItem cartItem = cartItemService.createCartItem(dto);
        return ResponseEntity.ok(toResponse(cartItem));
    }

    @GetMapping("{cartId}")
    @Operation(summary = "Get cart item by id")
    public ResponseEntity<CartItemResponse> getCartItemById(@PathVariable UUID cartId){
        CartItem cartItem = cartItemService.getCartById(cartId);
        return ResponseEntity.ok(toResponse(cartItem));
    }

    @GetMapping
    @Operation(summary = "Get all cart items")
    public ResponseEntity<Page<CartItemResponse>> getAllCartItems(Pageable pageable){
        Page<CartItemResponse> cartItems = cartItemService.allCarts(pageable).map(this::toResponse);
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("{cartId}")
    @Operation(summary = "Update cart item")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable UUID cartId, @RequestBody CartItemDTO dto){
        CartItem cartItem = cartItemService.updateCart(cartId, dto);
        return ResponseEntity.ok(toResponse(cartItem));
    }

    @DeleteMapping("{cartId}")
    @Operation(summary = "Delete cart item")
    public ResponseEntity<Void> deleteCartItem(@PathVariable UUID cartId){
        cartItemService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

    public CartItemResponse toResponse(CartItem cartItem){
        return new CartItemResponse(
                cartItem.getCartId(),
                cartItem.getProduct().getProductId(),
                cartItem.getQuantity()
        );
    }
}
