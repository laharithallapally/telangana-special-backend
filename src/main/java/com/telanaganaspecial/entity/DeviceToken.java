package com.telanaganaspecial.entity;

import jakarta.persistence.*;

import java.time.Instant;
@Entity
@Table(name = "device_tokens")
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK to your existing User entity's id — adjust type if your User id isn't Long
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fcm_token", nullable = false, unique = true, length = 512)
    private String fcmToken;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public DeviceToken() {}

    public DeviceToken(Long userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public Instant getCreatedAt() { return createdAt; }
}

