package com.ecommerce.controller;

import com.ecommerce.dto.CartItemDto;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;
    private final UserService userService;

    private User getOrCreateSessionUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            // Default demo customer if no session logged in
            user = userService.findByEmail("customer@apexstore.com")
                    .orElseGet(() -> userService.getAllUsers().stream().findFirst().orElse(null));
            if (user != null) {
                session.setAttribute("currentUser", user);
            }
        }
        return user;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active user session"));
        }

        List<CartItem> items = cartService.getCartItems(user);
        BigDecimal total = cartService.getCartTotal(user);
        int count = cartService.getCartItemCount(user);

        return ResponseEntity.ok(Map.of(
                "items", items,
                "total", total,
                "itemCount", count
        ));
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartItemDto cartItemDto, HttpSession session) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active user session"));
        }

        CartItem item = cartService.addToCart(user, cartItemDto.getProductId(), cartItemDto.getQuantity());
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity, HttpSession session) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active user session"));
        }

        CartItem updated = cartService.updateQuantity(user, id, quantity);
        return ResponseEntity.ok(Map.of("message", "Cart updated", "item", updated != null ? updated : "Removed"));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id, HttpSession session) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active user session"));
        }

        cartService.removeFromCart(user, id);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(HttpSession session) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active user session"));
        }

        cartService.clearCart(user);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}
