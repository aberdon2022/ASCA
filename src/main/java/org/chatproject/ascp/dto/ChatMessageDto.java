package org.chatproject.ascp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessageDto {
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;
}