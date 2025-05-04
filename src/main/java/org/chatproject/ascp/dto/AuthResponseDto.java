package org.chatproject.ascp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.chatproject.ascp.models.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponseDto(
        String displayName,
        String email,
        String jwt
) {
    public AuthResponseDto(User user, String jwt) {
        this(user.getDisplayName(), user.getEmail(), jwt);
    }
}
