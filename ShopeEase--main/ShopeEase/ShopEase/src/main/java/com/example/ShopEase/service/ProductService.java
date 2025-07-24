package com.example.ShopEase.service;
import com.example.ShopEase.model.Product;
import com.example.ShopEase.repository.ProductRepository;
import com.example.ShopEase.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Data

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    public List<Product> getByCategoryId(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    }

