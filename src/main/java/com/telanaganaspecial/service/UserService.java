package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.*;
import jakarta.validation.Valid;

public interface UserService {
    AuthResponseDto register(@Valid RegisterRequestDto dto);
    AuthResponseDto login(@Valid LoginRequestDto dto);
    UserProfileDto getProfile(String email);
    UserProfileDto updateProfile(String email, UpdateProfileRequestDto dto);
    void forgotPassword(ForgotPasswordRequestDto dto);
    void resetPassword(ResetPasswordRequestDto dto);
}


