package com.ecommerce.controller;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebCatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    private void populateCommonAttributes(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        int cartCount = 0;
        if (currentUser != null) {
            cartCount = cartService.getCartItemCount(currentUser);
        }
        model.addAttribute("cartCount", cartCount);
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        return "index";
    }

    @GetMapping("/products")
    public String catalog(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            Model model,
            HttpSession session) {
        populateCommonAttributes(model, session);

        List<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("currentSearch", search);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "catalog";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        model.addAttribute("product", product);
        return "product-details";
    }
}
