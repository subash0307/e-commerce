package com.ecommerce.controller;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Admin Panel (CRUD Operations & Management).
 *
 * Architectural Note (Interview Prep):
 * Role-Based Access Control (RBAC): Enforces that only users with ROLE_ADMIN
 * can access administrative routes and perform mutations on products and orders.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class WebAdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final UserService userService;

    private boolean checkAdminAccess(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null && "ROLE_ADMIN".equals(user.getRole().name());
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        BigDecimal revenue = orderService.getTotalRevenue();
        long totalOrders = orderService.countOrders();
        long totalProducts = productService.countProducts();
        long totalUsers = userService.countUsers();
        List<Order> recentOrders = orderService.getAllOrders().stream().limit(5).toList();

        model.addAttribute("revenue", revenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("recentOrders", recentOrders);
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String manageProducts(Model model, HttpSession session) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model, HttpSession session) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .build();

        model.addAttribute("productDto", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute ProductDto productDto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        try {
            if (productDto.getId() != null) {
                productService.updateProduct(productDto.getId(), productDto);
                redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            } else {
                productService.createProduct(productDto);
                redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model, HttpSession session) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("allStatuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, HttpSession session) {
        if (!checkAdminAccess(session)) {
            return "redirect:/login";
        }

        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
}
