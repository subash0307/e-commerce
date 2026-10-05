package com.ecommerce.service;

import com.ecommerce.dto.CheckoutDto;
import com.ecommerce.entity.*;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of OrderService managing the checkout lifecycle and inventory deduction.
 *
 * Architectural Note (Interview Prep):
 * - @Transactional ensures atomicity: if an inventory update or order item save fails,
 *   the entire checkout transaction rolls back, preventing inconsistent database states.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Order placeOrder(User user, CheckoutDto checkoutDto) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty shopping cart.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .shippingAddress(checkoutDto.getShippingAddress())
                .paymentMethod(checkoutDto.getPaymentMethod())
                .status(OrderStatus.PROCESSING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Validate inventory availability
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient inventory for product: " + product.getName());
            }

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Create OrderItem snapshot
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order creation
        cartItemRepository.deleteByUser(user);

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrders() {
        return orderRepository.count();
    }
}
