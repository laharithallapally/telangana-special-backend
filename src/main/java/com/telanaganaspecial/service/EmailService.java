package com.telanaganaspecial.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${SENDGRID_API_KEY:}")
    private String sendGridApiKey;

    @Value("${SENDGRID_FROM_EMAIL:}")
    private String fromEmail;

    @Async
    public void sendOrderStatusEmail(String toEmail, String customerName,
                                     Long orderId, String status) {
        String subject = "Order #" + orderId + " Status Update - Telangana Special";
        String body = buildEmailBody(customerName, orderId, status);
        send(toEmail, subject, body);
    }

    @Async
    public void sendOrderConfirmationEmail(String toEmail, String customerName,
                                           Long orderId, Double totalAmount) {
        String subject = "Order Confirmed! #" + orderId + " - Telangana Special";
        String body = buildConfirmationBody(customerName, orderId, totalAmount);
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String body) {
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            log.warn("SENDGRID_API_KEY not configured — skipping email to {}", toEmail);
            return;
        }

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent to {}", toEmail);
            } else {
                log.error("SendGrid failed for {}: status {} body {}",
                        toEmail, response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
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