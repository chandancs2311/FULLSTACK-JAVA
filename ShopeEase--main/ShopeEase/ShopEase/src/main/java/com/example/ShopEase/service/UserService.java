package com.example.ShopEase.service;

import com.example.ShopEase.config.JwtUtil;
import com.example.ShopEase.model.Cart;
import com.example.ShopEase.model.User;
import com.example.ShopEase.repository.CartRepository;
import com.example.ShopEase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Register new user
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ensure correct role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        } else if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole().toUpperCase());
        }

        // 1. Save user first
        User savedUser = userRepository.save(user);

        // 2. Create cart for the user
        Cart cart = new Cart();
        cart.setUser(savedUser); // assuming cart has @OneToOne User
        cart.setCreatedAt(new Date());
        cartRepository.save(cart); // cart is now linked to the user

        return savedUser;
    }

    public Map<String, Object> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("role", user.getRole());
                response.put("email", user.getEmail());
                return response;
            }
        }

        throw new RuntimeException("Invalid email or password");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isValidUser(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent() &&
                passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
