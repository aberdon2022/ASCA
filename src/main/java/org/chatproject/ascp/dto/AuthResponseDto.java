package org.chatproject.ascp.dto;

import org.chatproject.ascp.models.User;

public record AuthResponseDto(
        String displayName,
        String email,
        String jwt
) {
    public AuthResponseDto(User user, String jwt) {
        this(user.getDisplayName(), user.getEmail(), jwt);
    }
}
