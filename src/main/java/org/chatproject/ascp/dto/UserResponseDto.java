package org.chatproject.ascp.dto;

public class UserResponseDto {
    private String displayName;
    private String email;

    public UserResponseDto(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}
