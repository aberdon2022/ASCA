package org.chatproject.ascp.repository;

import org.chatproject.ascp.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Override
    Optional<ChatMessage> findById(Long id);
}
