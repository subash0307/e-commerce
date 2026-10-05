package com.ecommerce.controller;

import com.ecommerce.dto.CheckoutDto;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebCartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    private User getOrCreateSessionUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            user = userService.findByEmail("customer@apexstore.com")
                    .orElseGet(() -> userService.getAllUsers().stream().findFirst().orElse(null));
            if (user != null) {
                session.setAttribute("currentUser", user);
            }
        }
        return user;
    }

    private void populateCommonAttributes(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        int cartCount = 0;
        if (currentUser != null) {
            cartCount = cartService.getCartItemCount(currentUser);
        }
        model.addAttribute("cartCount", cartCount);
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        BigDecimal total = cartService.getCartTotal(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.addToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateQuantity(
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.updateQuantity(user, cartItemId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartService.removeFromCart(user, id);
        redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal total = cartService.getCartTotal(user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/checkout/confirm")
    public String processCheckout(
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            CheckoutDto checkoutDto = CheckoutDto.builder()
                    .shippingAddress(shippingAddress)
                    .paymentMethod(paymentMethod)
                    .build();

            Order order = orderService.placeOrder(user, checkoutDto);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + order.getOrderNumber() + " placed successfully!");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/checkout";
        }
    }
}
