package com.telanaganaspecial.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Double price;
    private Boolean isVeg;
    private Boolean available;
}
