package org.chatproject.ascp.services;

import org.chatproject.ascp.dto.ChatMessageDto;
import org.chatproject.ascp.models.ChatMessage;
import org.chatproject.ascp.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessageDto> getChatMessages(String sender, String receiver) {
        return chatMessageRepository.findBySenderAndReceiverOrReceiverAndSender(sender, receiver, receiver, sender)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ChatMessageDto toDto(ChatMessage m) {
        String ts = m.getTimestamp() != null
                ? m.getTimestamp().format(FORMATTER)
                : "";
        return new ChatMessageDto(
                m.getSender().getUsername(),
                m.getReceiver().getUsername(),
                m.getContent(),
                ts
        );
    }
}
