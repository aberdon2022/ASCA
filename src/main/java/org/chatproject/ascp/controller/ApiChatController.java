package org.chatproject.ascp.controller;

import org.chatproject.ascp.dto.ChatMessageDto;
import org.chatproject.ascp.services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/chat")
public class ApiChatController {
    private final ChatService chatService;

    public ApiChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendChatMessage(@RequestBody ChatMessageDto chatMessageDto) {
        chatService.sendMessage(chatMessageDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
