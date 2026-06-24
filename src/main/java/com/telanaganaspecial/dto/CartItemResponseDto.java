package com.telanaganaspecial.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CartItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}

