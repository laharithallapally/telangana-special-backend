package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.UpdateProfileRequestDto;
import com.telanaganaspecial.dto.UserProfileDto;
import com.telanaganaspecial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get my profile", description = "Returns the profile of the currently logged-in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved",
                    content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @Operation(summary = "Update my profile", description = "Updates name and phone of the currently logged-in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody UpdateProfileRequestDto dto) {
        System.out.println("=== UPDATE PROFILE HIT ===");
        System.out.println("email=[" + email + "]");
        System.out.println("dto.name=[" + dto.getName() + "]");
        System.out.println("dto.phone=[" + dto.getPhone() + "]");
        System.out.println("dto.gender=[" + dto.getGender() + "]");
        return ResponseEntity.ok(userService.updateProfile(email, dto));
    }

    @PostMapping("/debug-echo")
    public ResponseEntity<UpdateProfileRequestDto> debugEcho(@RequestBody UpdateProfileRequestDto dto) {
        System.out.println("DEBUG name=[" + dto.getName() + "] phone=[" + dto.getPhone() + "] gender=[" + dto.getGender() + "]");
        return ResponseEntity.ok(dto);
    }
}