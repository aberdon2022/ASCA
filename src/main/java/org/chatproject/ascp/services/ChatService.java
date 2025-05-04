package org.chatproject.ascp.services;

import jakarta.transaction.Transactional;
import org.chatproject.ascp.dto.ChatMessageDto;
import org.chatproject.ascp.models.ChatMessage;
import org.chatproject.ascp.models.ChatStatus;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.ChatMessageRepository;
import org.chatproject.ascp.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate template;
    private final UserRepository userRepository;

    public ChatService(ChatMessageRepository chatMessageRepository, SimpMessagingTemplate template, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.template = template;
        this.userRepository = userRepository;
    }
    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto) {

        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(() -> new RuntimeException("User not found"));

        User receiver = userRepository.findByUsername(chatMessageDto.receiverUsername())
                .orElseThrow(() -> new IllegalArgumentException("Receiver does not exist"));

        if (!receiver.getDisplayName().equals(chatMessageDto.receiverDisplayName())) {
            throw new IllegalArgumentException("Invalid receiver display name");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(senderEmail);
        chatMessage.setReceiver(receiver.getEmail());
        chatMessage.setSenderDisplayName(sender.getDisplayName());
        chatMessage.setReceiverDisplayName(receiver.getDisplayName());
        chatMessage.setContent(chatMessageDto.content());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setStatus(ChatStatus.SENT);
        chatMessageRepository.save(chatMessage);

        ChatMessageDto responseDto = new ChatMessageDto(
                receiver.getUsername(),
                sender.getDisplayName(),
                receiver.getDisplayName(),
                chatMessage.getContent()
        );

        String destination = "/topic/messages/" + receiver.getEmail();
        template.convertAndSend(destination, responseDto);
    }
}
