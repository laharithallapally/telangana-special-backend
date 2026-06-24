package com.telanaganaspecial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;
    @NotNull(message = "Price is required")

    private Double price;
    private String image;
    private Boolean available;
    @NotBlank(message = "category is required")
    private String category;
    @NotNull(message = "stock is required")
    @Min(value = 0,message = "stock cannot ge negative")
    private Integer stock;

    }

