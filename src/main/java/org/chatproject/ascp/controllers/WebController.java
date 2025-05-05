package org.chatproject.ascp.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.chatproject.ascp.dto.LoginDto;
import org.chatproject.ascp.dto.RegisterDto;
import org.chatproject.ascp.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
public class WebController {
    private final AuthService authService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public void loginPost(@Valid LoginDto loginDto) {
        authService.loginWeb(loginDto);
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid RegisterDto registerDto) {
        authService.registerUser(registerDto);
        return "redirect:/";
    }
}
