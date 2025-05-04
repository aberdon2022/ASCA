package org.chatproject.ascp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto (
        @NotBlank String username,
        @NotBlank @Size(min = 8) String password
) {}