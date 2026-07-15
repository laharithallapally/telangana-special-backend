package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.*;
import com.telanaganaspecial.entity.Role;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.exception.InvalidResetTokenException;
import com.telanaganaspecial.exception.UserAlreadyExistsException;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.security.JwtUtil;
import com.telanaganaspecial.service.EmailService;
import com.telanaganaspecial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    /** Normalize emails consistently everywhere so case/whitespace never causes a mismatch. */
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto dto) {
        String email = normalizeEmail(dto.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User user = User.builder()
                .name(dto.getName())
                .email(email)
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponseDto.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        String email = normalizeEmail(dto.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponseDto.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public UserProfileDto getProfile(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UserNotFoundException(email));

        return mapToProfileDto(user);
    }

    @Override
    public UserProfileDto updateProfile(String email, UpdateProfileRequestDto dto) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);

        return mapToProfileDto(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto dto) {
        // Always behave the same way whether or not the email exists,
        // so we don't leak which emails are registered.
        userRepository.findByEmail(normalizeEmail(dto.getEmail())).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
            userRepository.save(user);

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);
        });
    }

    @Override
    public void resetPassword(ResetPasswordRequestDto dto) {
        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid or expired reset link"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Reset link has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }


    private UserProfileDto mapToProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();
    }

}