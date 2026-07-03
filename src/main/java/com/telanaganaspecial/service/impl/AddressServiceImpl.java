package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.AddressRequestDto;
import com.telanaganaspecial.dto.AddressResponseDto;
import com.telanaganaspecial.entity.Address;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.exception.AddressNotFoundException;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.AddressRepository;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponseDto addAddress(String email, AddressRequestDto dto) {
        User user = getUser(email);

        boolean isFirstAddress = addressRepository.findByUserId(user.getId()).isEmpty();

        // first address a user adds automatically becomes their default
        boolean makeDefault = isFirstAddress || Boolean.TRUE.equals(dto.getIsDefault());

        if (makeDefault) {
            clearExistingDefault(user.getId());
        }

        Address address = Address.builder()
                .user(user)
                .label(dto.getLabel())
                .addressLine(dto.getAddressLine())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .isDefault(makeDefault)
                .build();

        addressRepository.save(address);
        return mapToDto(address);
    }

    @Override
    public List<AddressResponseDto> getMyAddresses(String email) {
        User user = getUser(email);
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public AddressResponseDto updateAddress(String email, Long addressId, AddressRequestDto dto) {
        User user = getUser(email);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        address.setLabel(dto.getLabel());
        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());

        if (Boolean.TRUE.equals(dto.getIsDefault()) && !address.getIsDefault()) {
            clearExistingDefault(user.getId());
            address.setIsDefault(true);
        }

        addressRepository.save(address);
        return mapToDto(address);
    }

    @Override
    public void deleteAddress(String email, Long addressId) {
        User user = getUser(email);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        boolean wasDefault = address.getIsDefault();
        addressRepository.delete(address);

        // if the deleted address was the default, promote another one automatically
        if (wasDefault) {
            addressRepository.findByUserId(user.getId()).stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setIsDefault(true);
                        addressRepository.save(next);
                    });
        }
    }

    @Override
    public AddressResponseDto setDefaultAddress(String email, Long addressId) {
        User user = getUser(email);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        clearExistingDefault(user.getId());
        address.setIsDefault(true);
        addressRepository.save(address);
        return mapToDto(address);
    }

    private void clearExistingDefault(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existing -> {
                    existing.setIsDefault(false);
                    addressRepository.save(existing);
                });
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private AddressResponseDto mapToDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .label(address.getLabel())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .isDefault(address.getIsDefault())
                .build();
    }
}