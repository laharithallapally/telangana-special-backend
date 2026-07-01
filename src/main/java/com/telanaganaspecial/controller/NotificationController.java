package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.NotificationResponseDto;
import com.telanaganaspecial.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(notificationService.getMyNotifications(email));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(email)));
    }

    @PutMapping("/mark-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal String email) {
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok().build();
    }
}

