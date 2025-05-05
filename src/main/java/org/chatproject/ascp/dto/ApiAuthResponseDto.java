package org.chatproject.ascp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.chatproject.ascp.models.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiAuthResponseDto(
        String username,
        String jwt
) {
    public ApiAuthResponseDto(User user, String jwt) {
        this(user.getUsername(), jwt);
    }
}
