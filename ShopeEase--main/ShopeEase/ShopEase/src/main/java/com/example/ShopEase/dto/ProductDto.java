package com.example.ShopEase.dto;

import lombok.Getter;
import lombok.Setter;
import java.lang.String;
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double cost;
    private String imageUrl;
    private int stock;
    private String category;
}
