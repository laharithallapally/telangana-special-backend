package com.telanaganaspecial.dto;



import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CartResponseDto  {
    private List<CartItemResponseDto> items;
    private Double grandTotal;
    private Integer totalItems; // number of items
}
