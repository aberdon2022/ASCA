package org.chatproject.ascp.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String sender;
    private String receiver;
    private String senderDisplayName;
    private String receiverDisplayName;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    public ChatMessage() {}

    public ChatMessage(String content, String sender, String receiver, LocalDateTime timestamp, ChatStatus status) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ChatStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiverDisplayName() {
        return receiverDisplayName;
    }

    public void setReceiverDisplayName(String receiverDisplayName) {
        this.receiverDisplayName = receiverDisplayName;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }
}

