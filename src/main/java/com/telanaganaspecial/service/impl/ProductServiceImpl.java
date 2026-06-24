package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.ProductRequestDto;
import com.telanaganaspecial.dto.ProductResponseDto;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.exception.ProductNotFoundException;
import com.telanaganaspecial.mapper.ProductMapper;
import com.telanaganaspecial.repository.CartItemRepository;
import com.telanaganaspecial.repository.OrderItemRepository;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto dto) {
        Product product = ProductMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return ProductMapper.toDto(saved);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toDto(product);
    }

    @Override
    public List<ProductResponseDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setImage(dto.getImage());
        existing.setAvailable(dto.getAvailable() == null ? existing.getAvailable() : dto.getAvailable());
        existing.setCategory(dto.getCategory());
        existing.setStock(dto.getStock());

        Product updated = productRepository.save(existing);
        return ProductMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // delete from cart_items first to avoid foreign key error
        cartItemRepository.deleteByProduct(product);

        orderItemRepository.deleteByProduct(product);
        // then delete product
        productRepository.delete(product);
    }
}