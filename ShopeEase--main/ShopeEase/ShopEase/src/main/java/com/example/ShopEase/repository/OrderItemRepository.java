package com.example.ShopEase.repository;

import com.example.ShopEase.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get order items by order ID (valid)
    List<OrderItem> findByOrder_OrderId(Long orderId);



    // Correct way to get items by user ID via Order -> User
    List<OrderItem> findByOrder_UserId(Long userId);
}
