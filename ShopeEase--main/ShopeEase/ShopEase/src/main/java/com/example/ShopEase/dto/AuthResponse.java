package com.example.ShopEase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.lang.String;
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
}
