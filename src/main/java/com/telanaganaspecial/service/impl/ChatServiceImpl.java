package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.ChatResponseDto;
import com.telanaganaspecial.dto.ProductResponseDto;
import com.telanaganaspecial.service.ChatService;
import com.telanaganaspecial.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final List<String> SPICY_KEYWORDS = List.of(
            "peri peri", "spicy", "spice", "chilli", "chili", "tornado", "blast"
    );

    private final ProductService productService;

    @Override
    public ChatResponseDto ask(String userMessage) {
        String msg = userMessage == null ? "" : userMessage.toLowerCase();
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        // Greeting
        if (containsAny(msg, "hi", "hello", "hey", "namaste")) {
            return reply("Hi there! 🌶️ Ask me about our menu — what's spicy, what's veg, "
                    + "or what to order for a group. What are you in the mood for?");
        }

        // Spicy
        if (containsAny(msg, "spicy", "spice", "chilli", "chili", "hot")) {
            List<ProductResponseDto> spicy = allProducts.stream()
                    .filter(p -> containsAny(safe(p.getName()) + " " + safe(p.getDescription()), SPICY_KEYWORDS.toArray(new String[0])))
                    .collect(Collectors.toList());
            if (spicy.isEmpty()) {
                return reply("Nothing super spicy on the menu right now, but Peri Peri Tornado and "
                        + "Crunchy Peri Peri Blast have the most kick!");
            }
            return reply(buildItemListReply("Here's what's spicy on our menu:", spicy));
        }

        // Non-veg
        if (containsAny(msg, "non-veg", "non veg", "nonveg", "meat", "chicken")) {
            List<ProductResponseDto> nonVeg = allProducts.stream()
                    .filter(p -> Boolean.FALSE.equals(p.getIsVeg()))
                    .collect(Collectors.toList());
            if (nonVeg.isEmpty()) {
                return reply("Everything on our menu is currently vegetarian! 🥦 "
                        + "But it's all made fresh and homemade — give it a try.");
            }
            return reply(buildItemListReply("Here are our non-veg options:", nonVeg));
        }

        // Veg
        if (containsAny(msg, "veg", "vegetarian")) {
            List<ProductResponseDto> veg = allProducts.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getIsVeg()))
                    .collect(Collectors.toList());
            return reply(buildItemListReply("Here are our vegetarian options:", veg));
        }

        // Recommendation / group / people
        if (containsAny(msg, "recommend", "suggest", "people", "group", "best", "popular", "top")) {
            List<ProductResponseDto> topRated = allProducts.stream()
                    .filter(p -> p.getAverageRating() != null && p.getAverageRating() > 0)
                    .sorted(Comparator.comparing(ProductResponseDto::getAverageRating).reversed())
                    .limit(3)
                    .collect(Collectors.toList());
            if (topRated.isEmpty()) {
                topRated = allProducts.stream().limit(3).collect(Collectors.toList());
            }
            return reply(buildItemListReply("Here are some customer favorites:", topRated));
        }

        // Budget / price
        if (containsAny(msg, "cheap", "budget", "under", "affordable", "price")) {
            List<ProductResponseDto> affordable = allProducts.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice() <= 50)
                    .sorted(Comparator.comparing(ProductResponseDto::getPrice))
                    .collect(Collectors.toList());
            if (affordable.isEmpty()) {
                return reply("Most of our items are priced between ₹40-₹90. "
                        + "Check out the full menu for exact prices!");
            }
            return reply(buildItemListReply("Here are our most budget-friendly options:", affordable));
        }

        // Category-based search (matches any word in the message against category names)
        List<ProductResponseDto> categoryMatch = allProducts.stream()
                .filter(p -> p.getCategory() != null && msg.contains(p.getCategory().trim().toLowerCase()))
                .collect(Collectors.toList());
        if (!categoryMatch.isEmpty()) {
            return reply(buildItemListReply("Here's what we have in that category:", categoryMatch));
        }

        // Fallback
        return reply("I can help with questions like \"what's spicy?\", \"what's vegetarian?\", "
                + "or \"what do you recommend?\". You can also browse our full menu above, "
                + "or message us directly on WhatsApp for anything else!");
    }

    private String buildItemListReply(String intro, List<ProductResponseDto> items) {
        StringBuilder sb = new StringBuilder(intro).append(" ");
        List<String> parts = items.stream()
                .limit(4)
                .map(p -> p.getName().trim() + " (₹" + formatPrice(p.getPrice()) + ")")
                .collect(Collectors.toList());
        sb.append(String.join(", ", parts));
        sb.append(".");
        return sb.toString();
    }

    private String formatPrice(Double price) {
        if (price == null) return "-";
        if (price == Math.floor(price)) {
            return String.valueOf(price.intValue());
        }
        return String.valueOf(price);
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private ChatResponseDto reply(String text) {
        return ChatResponseDto.builder().reply(text).build();
    }
}