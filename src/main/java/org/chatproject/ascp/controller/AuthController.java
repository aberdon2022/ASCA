package org.chatproject.ascp.controller;

import jakarta.validation.Valid;
import org.chatproject.ascp.dto.UserDto;
import org.chatproject.ascp.dto.UserResponseDto;
import org.chatproject.ascp.models.User;
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
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserDto userDto) {
        try {
            authService.registerUser(userDto);
            UserResponseDto userResponseDto = new UserResponseDto(userDto.getDisplayName(), userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@RequestBody @Valid UserDto userDto) {
        try {
            authService.loginUser(userDto);
            UserResponseDto userResponseDto = new UserResponseDto(userDto.getDisplayName(), userDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
