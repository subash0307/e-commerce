package com.ecommerce.service;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    List<CartItem> getCartItems(User user);
    CartItem addToCart(User user, Long productId, Integer quantity);
    CartItem updateQuantity(User user, Long cartItemId, Integer quantity);
    void removeFromCart(User user, Long cartItemId);
    void clearCart(User user);
    BigDecimal getCartTotal(User user);
    int getCartItemCount(User user);
}
