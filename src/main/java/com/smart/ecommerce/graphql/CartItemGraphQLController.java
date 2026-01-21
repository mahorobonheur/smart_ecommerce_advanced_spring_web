package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.request.CartItemDTO;
import com.smart.ecommerce.dto.response.CartItemResponse;
import com.smart.ecommerce.model.CartItem;
import com.smart.ecommerce.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class CartItemGraphQLController {

    @Autowired
    private CartItemService cartItemService;

    @MutationMapping
    public CartItemResponse createCartItem(@Argument CartItemDTO input) {
        CartItem cartItem = cartItemService.createCartItem(input);
        return toResponse(cartItem);
    }

    @QueryMapping
    public CartItemResponse cartItemById(@Argument String cartId) {
        CartItem cartItem = cartItemService.getCartById(UUID.fromString(cartId));
        return toResponse(cartItem);
    }

    @MutationMapping
    public CartItemResponse updateCartItem(@Argument String cartId,
                                           @Argument CartItemDTO input) {
        CartItem cartItem = cartItemService.updateCart(UUID.fromString(cartId), input);
        return toResponse(cartItem);
    }

    @MutationMapping
    public Boolean deleteCartItem(@Argument String cartId) {
        cartItemService.deleteCart(UUID.fromString(cartId));
        return true;
    }

    private CartItemResponse toResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getCartId(),
                cartItem.getProduct().getProductId(),
                cartItem.getQuantity()
        );
    }
}
