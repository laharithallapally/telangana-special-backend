package com.telanaganaspecial.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



@Data
public class PlaceOrderRequestDto {
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
}
