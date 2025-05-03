package org.chatproject.ascp.controller;

import jakarta.validation.Valid;
import org.chatproject.ascp.dto.AuthResponseDto;
import org.chatproject.ascp.dto.UserDto;
import org.chatproject.ascp.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@RequestBody @Valid UserDto userDto) {
        AuthResponseDto authResponseDto = authService.registerUser(userDto);
        if (authResponseDto.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponseDto);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody @Valid UserDto userDto) {
        AuthResponseDto authResponseDto = authService.loginUser(userDto);
        if (authResponseDto.getError() != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
    }
}
