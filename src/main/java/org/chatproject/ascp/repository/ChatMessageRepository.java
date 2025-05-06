package org.chatproject.ascp.repository;

import org.chatproject.ascp.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Override
    Optional<ChatMessage> findById(Long id);
    @Query("SELECT m FROM ChatMessage m WHERE (m.sender.username = ?1 AND m.receiver.username = ?2) OR (m.receiver.username = ?3 AND m.sender.username = ?4)")
    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSender(String senderUsername, String receiverUsername, String sender2Username, String receiver2Username);
}
