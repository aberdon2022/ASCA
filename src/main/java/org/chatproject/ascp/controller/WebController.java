package org.chatproject.ascp.controller;

import org.chatproject.ascp.dto.RegisterDto;
import org.chatproject.ascp.dto.UserSearchDto;
import org.chatproject.ascp.exceptions.EmailAlreadyRegisteredException;
import org.chatproject.ascp.services.AuthService;
import org.chatproject.ascp.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {
    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public WebController(AuthService authService, UserService userService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute RegisterDto registerDto, Model model) {
        try {
            authService.registerUser(registerDto);
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerDto.email(), registerDto.password())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/chat";
        } catch (EmailAlreadyRegisteredException e) {
            model.addAttribute("error", "Email already registered");
            model.addAttribute("registerDto", registerDto);
            return "register";
        }
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "displayName", required = false) String displayName, Model model) {
        if (displayName != null && !displayName.trim().isEmpty()) {
            List<UserSearchDto> users = userService.getUsersByDisplayName(displayName);
            model.addAttribute("users", users);
        }
        model.addAttribute("displayName", displayName != null ? displayName : "");
        return "search";
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUserEmail", auth.getName());
        return "chat";
    }
}
