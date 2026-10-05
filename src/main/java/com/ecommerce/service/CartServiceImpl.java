package com.ecommerce.service;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Override
    @Transactional
    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ")");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserAndProduct(user, product);
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Total requested quantity (" + newQuantity + ") exceeds available stock (" + product.getStockQuantity() + ")");
            }
            existingItem.setQuantity(newQuantity);
            return cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
            return cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public CartItem updateQuantity(User user, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: You do not own this cart item.");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + cartItem.getProduct().getStockQuantity() + ")");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: You do not own this cart item.");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(User user) {
        return cartItemRepository.findByUser(user).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        return cartItemRepository.findByUser(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
