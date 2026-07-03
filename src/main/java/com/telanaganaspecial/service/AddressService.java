package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.AddressRequestDto;
import com.telanaganaspecial.dto.AddressResponseDto;

import java.util.List;

public interface AddressService {
    AddressResponseDto addAddress(String email, AddressRequestDto dto);
    List<AddressResponseDto> getMyAddresses(String email);
    AddressResponseDto updateAddress(String email, Long addressId, AddressRequestDto dto);
    void deleteAddress(String email, Long addressId);
    AddressResponseDto setDefaultAddress(String email, Long addressId);
}