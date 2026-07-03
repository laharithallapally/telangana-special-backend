package com.telanaganaspecial.controller;
import com.telanaganaspecial.entity.DeviceToken;
import com.telanaganaspecial.repository.DeviceTokenRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class FcmTokenController {
    private final DeviceTokenRepository deviceTokenRepository;

    public FcmTokenController(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    @PostMapping("/fcm-token")
    public Map<String, String> saveToken(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        String token = body.get("token");
        if (token == null || token.isBlank()) {
            return Map.of("status", "error", "message", "token is required");
        }

        // Avoid duplicate rows if the browser re-sends the same token
        deviceTokenRepository.findByFcmToken(token)
                .ifPresentOrElse(
                        existing -> { /* already stored, nothing to do */ },
                        () -> deviceTokenRepository.save(new DeviceToken(userId, token))
                );

        return Map.of("status", "ok");
    }
}

