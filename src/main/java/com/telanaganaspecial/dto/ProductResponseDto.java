package com.telanaganaspecial.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String image;
    private Boolean available;
    private String category;
    private Integer stock;
}

