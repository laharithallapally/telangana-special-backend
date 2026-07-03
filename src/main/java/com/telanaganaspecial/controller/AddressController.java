package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.AddressRequestDto;
import com.telanaganaspecial.dto.AddressResponseDto;
import com.telanaganaspecial.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address API", description = "Saved delivery address endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Add a new saved address")
    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody AddressRequestDto dto) {
        return ResponseEntity.ok(addressService.addAddress(email, dto));
    }

    @Operation(summary = "Get my saved addresses")
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getMyAddresses(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(addressService.getMyAddresses(email));
    }

    @Operation(summary = "Update a saved address")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @AuthenticationPrincipal String email,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto dto) {
        return ResponseEntity.ok(addressService.updateAddress(email, addressId, dto));
    }

    @Operation(summary = "Delete a saved address")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal String email,
            @PathVariable Long addressId) {
        addressService.deleteAddress(email, addressId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Set an address as default")
    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @AuthenticationPrincipal String email,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(email, addressId));
    }
}