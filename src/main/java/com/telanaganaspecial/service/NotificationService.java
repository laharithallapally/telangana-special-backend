package com.telanaganaspecial.service;
import com.telanaganaspecial.dto.NotificationResponseDto;
import com.telanaganaspecial.entity.Notification;
import com.telanaganaspecial.entity.Role;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.repository.NotificationRepository;
import com.telanaganaspecial.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import com.telanaganaspecial.repository.DeviceTokenRepository;
import com.telanaganaspecial.entity.DeviceToken;
import java.util.List;

import org.springframework.stereotype.Service;




@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository;

    public void notifyUser(User user, String message) {
        Notification notification = Notification.builder()
                .recipient(user)
                .message(message)
                .build();
        notificationRepository.save(notification);
    }

    public void notifyAllAdmins(String message) {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .toList();
        for (User admin : admins) {
            notifyUser(admin, message);
        }
    }

    public List<NotificationResponseDto> getMyNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> NotificationResponseDto.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .isRead(n.getIsRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    public long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    // Playful message builders
    public static String orderPlacedCustomerMessage(String name) {
        return "🎉 Yay " + name + "! Your order is confirmed and our kitchen is getting started!";
    }

    public static String orderPlacedAdminMessage(String name, double amount) {
        return "💰 Cha-ching! " + name + " just placed a new order — ₹" + String.format("%.0f", amount) + "!";
    }

    // NOTE: this method deliberately does NOT import com.google.firebase.messaging.Notification
    // at the top of the file — it's fully-qualified inline below. Your own entity is also
    // named "Notification" (see the import at the top of this file), and Java doesn't allow
    // two imported classes to share the same simple name. Fully-qualifying the Firebase one
    // here avoids that collision without renaming anything else in this file.
    public void sendPush(Long userId, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUserId(userId);

        for (DeviceToken deviceToken : tokens) {
            Message message = Message.builder()
                    .setToken(deviceToken.getFcmToken())
                    .setNotification(
                            com.google.firebase.messaging.Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                deviceTokenRepository.deleteByFcmToken(deviceToken.getFcmToken());
            }
        }
    }

    public static String statusMessage(String name, String status) {
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> "✅ Woohoo " + name + "! Your order is confirmed and heading to our kitchen!";
            case "PREPARING" -> "👨‍🍳 Great news " + name + "! Your delicious order is being prepared with love!";
            case "OUT_FOR_DELIVERY" -> "🚚 It's on the way, " + name + "! Your order is zooming towards you!";
            case "DELIVERED" -> "🎊 Enjoy your meal, " + name + "! Thanks for choosing Telangana Special!";
            case "CANCELLED" -> "😢 Oh no " + name + ", your order was cancelled. Reach out if you need help!";
            default -> "📦 Hey " + name + ", your order status was updated!";
        };
    }
}