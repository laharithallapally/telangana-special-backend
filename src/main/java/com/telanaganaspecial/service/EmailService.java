package com.telanaganaspecial.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    @ConditionalOnProperty(name = "spring.mail.host")
    public class EmailService {

        private final JavaMailSender mailSender;

        @Async
        public void sendOrderStatusEmail(String toEmail, String customerName,
                                         Long orderId, String status) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Order #" + orderId + " Status Update - Telangana Special");
                message.setText(buildEmailBody(customerName, orderId, status));
                mailSender.send(message);
                log.info("Order status email sent to {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            }
        }

        @Async
        public void sendOrderConfirmationEmail(String toEmail, String customerName,
                                               Long orderId, Double totalAmount) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Order Confirmed! #" + orderId + " - Telangana Special");
                message.setText(buildConfirmationBody(customerName, orderId, totalAmount));
                mailSender.send(message);
                log.info("Order confirmation email sent to {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send confirmation email to {}: {}", toEmail, e.getMessage());
            }
        }

        private String buildEmailBody(String name, Long orderId, String status) {
            String statusMessage = switch (status.toUpperCase()) {
                case "PROCESSING" -> "Your order is being processed and will be dispatched soon.";
                case "SHIPPED" -> "Great news! Your order has been shipped and is on its way.";
                case "DELIVERED" -> "Your order has been delivered. Enjoy your Telangana Special products!";
                case "CANCELLED" -> "Your order has been cancelled. If you have any questions, please contact us.";
                default -> "Your order status has been updated.";
            };

            return """
                Hi %s,
                
                Your order #%d status has been updated to: %s
                
                %s
                
                Thank you for shopping with Telangana Special!
                
                Best regards,
                Telangana Special Team
                """.formatted(name, orderId, status, statusMessage);
        }

        private String buildConfirmationBody(String name, Long orderId, Double totalAmount) {
            return """
                Hi %s,
                
                Thank you for your order! We've received your order and it's being prepared.
                
                Order ID: #%d
                Total Amount: ₹%.2f
                Status: PENDING
                
                We'll notify you when your order status changes.
                
                Thank you for shopping with Telangana Special!
                
                Best regards,
                Telangana Special Team
                """.formatted(name, orderId, totalAmount);
        }
    }

