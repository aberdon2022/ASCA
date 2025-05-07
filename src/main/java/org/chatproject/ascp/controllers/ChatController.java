package org.chatproject.ascp.controllers;

import lombok.AllArgsConstructor;
import org.chatproject.ascp.dto.ChatMessageDto;
import org.chatproject.ascp.models.ChatMessage;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.ChatMessageRepository;
import org.chatproject.ascp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDto>> getMessages(@RequestParam("recipient") String recipient) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> sender = userRepository.findByUsername(currentUser);
        Optional<User> receiver = userRepository.findByUsername(recipient);

        if (sender.isEmpty() || receiver.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<ChatMessage> messages = chatMessageRepository.findBySenderAndReceiverOrReceiverAndSender(
                sender.get().getUsername(), receiver.get().getUsername(), sender.get().getUsername(), receiver.get().getUsername()
        );
        List<ChatMessageDto> chatMessages = messages.stream()
                .map(message -> new ChatMessageDto(
                        message.getSender().getUsername(),
                        message.getReceiver().getUsername(),
                        message.getContent(),
                        message.getTimestamp().format(formatter)
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatMessages);
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        String username = principal.getName();
        System.out.println("Message from " + username + " to " + chatMessageDto.getReceiver() + ": " + chatMessageDto.getContent());

        Optional<User> sender = userRepository.findByUsername(username);
        Optional<User> receiver = userRepository.findByUsername(chatMessageDto.getReceiver());

        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender.get());
        chatMessage.setReceiver(receiver.get());
        chatMessage.setContent(chatMessageDto.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        ChatMessageDto responseMessage = new ChatMessageDto(
                sender.get().getUsername(),
                receiver.get().getUsername(),
                chatMessageDto.getContent(),
                LocalDateTime.now().format(formatter)
        );

        System.out.println("Sending to receiver /user/" + receiver.get().getUsername() + "/queue/messages: " + responseMessage);
        messagingTemplate.convertAndSendToUser(receiver.get().getUsername(), "/queue/messages", responseMessage);

        System.out.println("Sending to sender /user/" + sender.get().getUsername() + "/queue/messages: " + responseMessage);
        messagingTemplate.convertAndSendToUser(sender.get().getUsername(), "/queue/messages", responseMessage);
    }
}