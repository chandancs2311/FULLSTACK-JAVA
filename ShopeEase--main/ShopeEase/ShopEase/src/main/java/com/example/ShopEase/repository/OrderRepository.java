package com.example.ShopEase.repository;

import com.example.ShopEase.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders by a specific user
    List<Order> findByUserId(Long userId);
}
