package org.chatproject.ascp.repository;

import org.chatproject.ascp.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
