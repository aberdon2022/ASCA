package org.chatproject.ascp.controllers;

import org.chatproject.ascp.dto.ApiAuthResponseDto;
import org.chatproject.ascp.dto.LoginDto;
import org.chatproject.ascp.dto.RegisterDto;
import org.chatproject.ascp.dto.RegisterResponseDto;
import org.chatproject.ascp.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class ApiAuthController {

    private final AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok(authService.registerUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiAuthResponseDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.loginApiUser(loginDto));
    }
}