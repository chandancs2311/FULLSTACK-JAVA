package com.example.ShopEase.dto;

import lombok.Getter;
import lombok.Setter;
import java.lang.String;
@Getter
@Setter
public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private String role;
}
