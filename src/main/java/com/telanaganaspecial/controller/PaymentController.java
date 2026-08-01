package com.telanaganaspecial.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.telanaganaspecial.dto.OrderResponseDto;
import com.telanaganaspecial.dto.PlaceOrderRequestDto;
import com.telanaganaspecial.service.CartService;
import com.telanaganaspecial.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Razorpay payment endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final CartService cartService;
    private final OrderService orderService;

    @Operation(summary = "Create Razorpay order from current cart")
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@AuthenticationPrincipal String email) {
        try {
            double cartTotal = cartService.getCart(email).getGrandTotal();
            if (cartTotal <= 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cart is empty");
                return ResponseEntity.badRequest().body(error);
            }
            int amountInPaise = (int) Math.round(cartTotal * 100);

            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());

            Order order = client.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", order.get("id"));
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("keyId", keyId);

            return ResponseEntity.ok(response);

        } catch (RazorpayException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Verify payment and place the order")
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @AuthenticationPrincipal String email,
            @RequestBody Map<String, String> request) {
        try {
            String razorpayOrderId = request.get("razorpay_order_id");
            String razorpayPaymentId = request.get("razorpay_payment_id");
            String razorpaySignature = request.get("razorpay_signature");
            String addressIdStr = request.get("addressId");

            String data = razorpayOrderId + "|" + razorpayPaymentId;

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey =
                    new javax.crypto.spec.SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            boolean isValid = hexString.toString().equals(razorpaySignature);

            if (!isValid) {
                Map<String, Object> error = new HashMap<>();
                error.put("verified", false);
                error.put("error", "Signature mismatch — payment could not be verified");
                return ResponseEntity.badRequest().body(error);
            }

            PlaceOrderRequestDto orderDto = new PlaceOrderRequestDto();
            if (addressIdStr != null && !addressIdStr.isBlank()) {
                orderDto.setAddressId(Long.parseLong(addressIdStr));
            }

            OrderResponseDto placedOrder = orderService.placeOrderWithPayment(
                    email, orderDto, razorpayOrderId, razorpayPaymentId);

            Map<String, Object> response = new HashMap<>();
            response.put("verified", true);
            response.put("order", placedOrder);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("verified", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}