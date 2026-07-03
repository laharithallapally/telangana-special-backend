package com.telanaganaspecial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {
    private Long id;
    private String label;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private Boolean isDefault;
}