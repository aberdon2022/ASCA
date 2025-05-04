package org.chatproject.ascp.controller;

import jakarta.validation.Valid;
import org.chatproject.ascp.dto.AuthResponseDto;
import org.chatproject.ascp.dto.LoginDto;
import org.chatproject.ascp.dto.RegisterDto;
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
    public ResponseEntity<AuthResponseDto> registerUser(@RequestBody @Valid RegisterDto registerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.loginUser(loginDto));
    }
}
