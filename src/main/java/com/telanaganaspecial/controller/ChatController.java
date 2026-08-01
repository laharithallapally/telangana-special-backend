package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.ChatRequestDto;
import com.telanaganaspecial.dto.ChatResponseDto;
import com.telanaganaspecial.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "AI menu assistant endpoints")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Ask the AI menu assistant a question")
    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(@Valid @RequestBody ChatRequestDto dto) {
        return ResponseEntity.ok(chatService.ask(dto.getMessage()));
    }
}