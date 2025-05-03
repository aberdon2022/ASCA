package org.chatproject.ascp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.chatproject.ascp.models.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDto {
    private String displayName;
    private String email;
    private String jwt;
    private String error;

    public AuthResponseDto(User user, String jwt) {
        if (user != null) {
            this.displayName = user.getDisplayName();
            this.email = user.getEmail();
        }
        this.jwt = jwt;
        this.error = null;
    }

    public AuthResponseDto(String error) {
        this.displayName = null;
        this.email = null;
        this.jwt = null;
        this.error = error;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
