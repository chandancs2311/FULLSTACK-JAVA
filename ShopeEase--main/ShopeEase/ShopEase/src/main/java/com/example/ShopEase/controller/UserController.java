package com.example.ShopEase.controller;

import com.example.ShopEase.config.JwtUtil;
import com.example.ShopEase.model.Cart;
import com.example.ShopEase.model.User;
import com.example.ShopEase.service.CartService;
import com.example.ShopEase.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String role = user.getRole();

        if (role == null || role.isEmpty()) {
            role = "ROLE_USER";
        } else if (!role.toUpperCase().startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        } else {
            role = role.toUpperCase();
        }

        user.setRole(role);

        //  Hash the password here!
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));


        User savedUser = userService.save(user);

        if ("ROLE_USER".equals(savedUser.getRole())) {
            Cart cart = cartService.createCart(savedUser);
            savedUser.setCart(cart);
            userService.save(savedUser); // Save again with cart
        }

        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

        //  Debug Print: Token and Role
        System.out.println("Generated JWT Token: " + token);
        System.out.println("Logged-in Role: " + user.getRole());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole(),
                "email", user.getEmail(),
                "userId", user.getId()
        ));
    }


    // Profile (for authenticated users)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

    // Admin-only profile access
    @GetMapping("/admin/profile")
    public ResponseEntity<?> getAdminProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        User admin = userService.getUserByEmail(email);

        if (admin == null || !"ROLE_ADMIN".equals(admin.getRole())) {
            return ResponseEntity.status(403).body("Access Denied: Not an Admin");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("admin", admin);
        response.put("allUsers", userService.getAllUsers());

        return ResponseEntity.ok(response);
    }
}
