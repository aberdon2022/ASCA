package org.chatproject.ascp.dto;

import lombok.Data;
import org.chatproject.ascp.models.User;

@Data
public class RegisterResponseDto {
    private String username;
    public RegisterResponseDto(User user) {
        this.username = user.getUsername();
    }
}
