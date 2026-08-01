package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.ChatResponseDto;

public interface ChatService {
    ChatResponseDto ask(String userMessage);
}