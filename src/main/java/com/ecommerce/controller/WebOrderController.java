package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebOrderController {

    private final OrderService orderService;
    private final CartService cartService;
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

    @GetMapping("/orders")
    public String myOrders(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        User user = getOrCreateSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));

        model.addAttribute("order", order);
        return "order-details";
    }
}
