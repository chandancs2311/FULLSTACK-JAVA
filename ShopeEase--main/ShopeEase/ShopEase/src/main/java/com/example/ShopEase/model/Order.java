package com.example.ShopEase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    // Reference to User ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Relation to User object
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "shipping_address", length = 100)
    private String shippingAddress;

    @Column(name = "status", length = 40)
    private String status;

    //  DON'T USE double for money!
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
}
