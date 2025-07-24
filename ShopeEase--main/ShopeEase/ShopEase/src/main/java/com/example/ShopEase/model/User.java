package com.example.ShopEase.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.String;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data //  Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    @Column(name = "role")
    private String role = "ROLE_USER"; // this handles defaulting in Java


    private LocalDateTime createdAt = LocalDateTime.now();
}
