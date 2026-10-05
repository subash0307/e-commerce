package com.ecommerce.service;

import com.ecommerce.dto.CheckoutDto;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order placeOrder(User user, CheckoutDto checkoutDto);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getOrderByOrderNumber(String orderNumber);
    List<Order> getOrdersByUser(User user);
    List<Order> getAllOrders();
    Order updateOrderStatus(Long orderId, OrderStatus status);
    BigDecimal getTotalRevenue();
    long countOrders();
}
