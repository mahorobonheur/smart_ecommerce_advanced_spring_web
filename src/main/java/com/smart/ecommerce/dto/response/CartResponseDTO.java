package com.smart.ecommerce.dto.response;
import com.smart.ecommerce.model.Cart;
import com.smart.ecommerce.model.CartItem;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CartResponseDTO {
    private UUID cartId;
    private UUID userId;
    private List<CartItemResponseDTO> items;

    public CartResponseDTO(UUID cartId, UUID userId, List<CartItemResponseDTO> items) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
    }

    public UUID getCartId() {
        return cartId;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<CartItemResponseDTO> getItems() {
        return items;
    }

    // Nested DTO
    public static class CartItemResponseDTO {
        private UUID productId;
        private int quantity;

        public CartItemResponseDTO(CartItem item) {
            this.productId = item.getProduct().getProductId();
            this.quantity = item.getQuantity();
        }

        public UUID getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }


    public static CartResponseDTO fromCart(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(CartItemResponseDTO::new)
                .collect(Collectors.toList());
        return new CartResponseDTO(cart.getCartId(), cart.getUser().getUserId(), items);
    }
}
