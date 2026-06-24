package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.ProductRequestDto;
import com.telanaganaspecial.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto addProduct(ProductRequestDto dto);
    List<ProductResponseDto> getAllProducts();
    ProductResponseDto getProductById(Long id);
    List<ProductResponseDto> getProductsByCategory(String category);
    ProductResponseDto updateProduct(Long id, ProductRequestDto dto);
    void deleteProduct(Long id);
}
