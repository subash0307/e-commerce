package com.ecommerce.config;

import com.ecommerce.dto.CheckoutDto;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Database Seeder populating demo catalog, admin user, and sample orders on startup.
 *
 * Architectural Note (Interview Prep):
 * - CommandLineRunner runs automatically after the Spring ApplicationContext is fully initialized.
 * - Ensures repeatable, turnkey environment setup for reviewers, recruiters, and dev teams.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderService orderService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data population.");
            return;
        }

        log.info("Seeding initial e-commerce data...");

        // 1. Seed Users (Admin & Customer)
        User admin = User.builder()
                .fullName("System Administrator")
                .email("admin@apexstore.com")
                .password(UserServiceImpl.hashPassword("admin123"))
                .role(Role.ROLE_ADMIN)
                .phone("+1 555-0199")
                .address("100 Innovation Way, Suite 400, San Francisco, CA")
                .build();
        userRepository.save(admin);

        User customer = User.builder()
                .fullName("Alex Morgan")
                .email("customer@apexstore.com")
                .password(UserServiceImpl.hashPassword("user123"))
                .role(Role.ROLE_CUSTOMER)
                .phone("+1 555-0142")
                .address("742 Evergreen Terrace, Springfield, OR")
                .build();
        userRepository.save(customer);

        // 2. Seed Categories
        Category electronics = Category.builder()
                .name("Electronics & Laptops")
                .description("Next-gen computing devices, ultra-thin laptops, and productivity gear.")
                .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80")
                .build();

        Category audio = Category.builder()
                .name("Audio & Wearables")
                .description("Noise-cancelling headphones, high-fidelity earbuds, and smart wearables.")
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80")
                .build();

        Category gaming = Category.builder()
                .name("Gaming & Gear")
                .description("Precision mechanical keyboards, ergonomic mice, and immersive displays.")
                .imageUrl("https://images.unsplash.com/photo-1612287233207-68cfda40d16c?w=800&auto=format&fit=crop&q=80")
                .build();

        Category accessories = Category.builder()
                .name("Accessories & Office")
                .description("Minimalist desk mats, USB-C thunderbolt docks, and ergonomic stands.")
                .imageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&auto=format&fit=crop&q=80")
                .build();

        categoryRepository.saveAll(List.of(electronics, audio, gaming, accessories));

        // 3. Seed Products
        Product p1 = Product.builder()
                .name("MacBook Pro 16 M3 Max")
                .description("Stunning Liquid Retina XDR display, up to 128GB unified memory, and unbelievable battery life.")
                .price(new BigDecimal("2499.00"))
                .stockQuantity(15)
                .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80")
                .category(electronics)
                .build();

        Product p2 = Product.builder()
                .name("Sony WH-1000XM5 Wireless Headphones")
                .description("Industry-leading active noise cancellation with 2 processors, 8 microphones, and Hi-Res audio.")
                .price(new BigDecimal("399.99"))
                .stockQuantity(28)
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80")
                .category(audio)
                .build();

        Product p3 = Product.builder()
                .name("Apex Pro TKL Mechanical Keyboard")
                .description("World's fastest OmniPoint 2.0 adjustable hypermagnetic switches with OLED smart display.")
                .price(new BigDecimal("189.95"))
                .stockQuantity(40)
                .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80")
                .category(gaming)
                .build();

        Product p4 = Product.builder()
                .name("Logitech MX Master 3S Mouse")
                .description("Quiet Clicks ergonomic wireless performance mouse with 8000 DPI track-on-glass sensor.")
                .price(new BigDecimal("99.99"))
                .stockQuantity(55)
                .imageUrl("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80")
                .category(accessories)
                .build();

        Product p5 = Product.builder()
                .name("Dell UltraSharp 34 Curved Thunderbolt Monitor")
                .description("WQHD curved screen with IPS Black technology, 98% DCI-P3 coverage, and built-in KVM.")
                .price(new BigDecimal("849.00"))
                .stockQuantity(12)
                .imageUrl("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80")
                .category(electronics)
                .build();

        Product p6 = Product.builder()
                .name("Apple AirPods Pro 2 (USB-C)")
                .description("Pro-level Active Noise Cancellation, Adaptive Audio, and Personalized Spatial Audio.")
                .price(new BigDecimal("249.00"))
                .stockQuantity(35)
                .imageUrl("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=800&auto=format&fit=crop&q=80")
                .category(audio)
                .build();

        productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));

        // 4. Seed an Initial Sample Order for Customer
        cartService.addToCart(customer, p2.getId(), 1);
        cartService.addToCart(customer, p4.getId(), 2);
        orderService.placeOrder(customer, CheckoutDto.builder()
                .shippingAddress("742 Evergreen Terrace, Springfield, OR")
                .paymentMethod("Credit Card (Visa ending in 4242)")
                .build());

        log.info("E-Commerce demo data seeded successfully!");
        log.info("Admin login: admin@apexstore.com / admin123");
        log.info("Customer login: customer@apexstore.com / user123");
    }
}
