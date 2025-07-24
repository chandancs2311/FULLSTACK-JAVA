package com.example.ShopEase.repository;

import com.example.ShopEase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user only by email
    Optional<User> findByEmail(String email);
}
