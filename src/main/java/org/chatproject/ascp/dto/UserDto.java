package org.chatproject.ascp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {
    @NotBlank
    private String displayName;
    @Size(min = 8)
    private String password;
    @Email
    @NotBlank
    private String email;

    protected UserDto() {
    }
    public UserDto(String displayName, String password, String email) {
        this.displayName = displayName;
        this.password = password;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
