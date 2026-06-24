package com.telanaganaspecial.mapper;

import com.telanaganaspecial.dto.ProductRequestDto;
import com.telanaganaspecial.dto.ProductResponseDto;
import com.telanaganaspecial.entity.Product;

public class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(ProductRequestDto dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .image(dto.getImage())
                .available(dto.getAvailable() == null ? true : dto.getAvailable())
                .category(dto.getCategory())
                .stock(dto.getStock())
                .build();
    }

    public static ProductResponseDto toDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .image(product.getImage())
                .available(product.getAvailable())
                .category(product.getCategory())
                .stock(product.getStock())
                .build();
    }
}
