package org.chatproject.ascp.dto;

import org.chatproject.ascp.models.User;

public class UserResponseDto {
    private String displayName;
    private String email;

    public UserResponseDto(User user) {
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }
}