package org.chatproject.ascp.dto;

public record ChatMessageDto(
        String receiverUsername,
        String senderDisplayName,
        String receiverDisplayName,
        String content
) {}
