package com.telanaganaspecial.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class OrderResponseDto {
    private Long id;
    private List<CartItemResponseDto> items;
    private Double totalAmount;
    private String status;
    private String deliveryAddress;
    private LocalDateTime createdAt;


}

