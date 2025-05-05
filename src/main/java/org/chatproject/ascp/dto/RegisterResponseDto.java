package org.chatproject.ascp.dto;

import org.chatproject.ascp.models.User;

public record RegisterResponseDto(String username) {
    public RegisterResponseDto(User user) {
        this(user.getUsername());
    }
}
