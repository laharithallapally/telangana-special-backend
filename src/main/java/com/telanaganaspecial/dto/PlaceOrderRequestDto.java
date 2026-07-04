package com.telanaganaspecial.dto;

import lombok.Data;

@Data
public class PlaceOrderRequestDto {
    private Long addressId;
    private String deliveryAddress;
}