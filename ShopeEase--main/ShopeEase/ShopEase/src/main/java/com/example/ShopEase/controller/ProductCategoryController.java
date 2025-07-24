package com.example.ShopEase.controller;

import com.example.ShopEase.model.ProductCategory;
import com.example.ShopEase.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryRepository categoryRepo;

    @GetMapping
    public List<ProductCategory> getAll() {
        return categoryRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody ProductCategory category) {
        categoryRepo.save(category);
        return ResponseEntity.ok("Category added");
    }
}
