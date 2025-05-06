package org.chatproject.ascp.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.chatproject.ascp.dto.LoginDto;
import org.chatproject.ascp.dto.RegisterDto;
import org.chatproject.ascp.dto.ChatMessageDto;
import org.chatproject.ascp.models.ChatMessage;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.ChatMessageRepository;
import org.chatproject.ascp.repository.UserRepository;
import org.chatproject.ascp.services.AuthService;
import org.chatproject.ascp.services.ChatService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
public class WebController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;

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

    @GetMapping("/chat")
    public String chat(
            @RequestParam(value = "recipient", required = false) String recipient,
            @RequestParam(value = "query", required = false) String query,
            Model model) {

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> sender = userRepository.findByUsername(currentUser);

        if (sender.isEmpty()) {
            throw new IllegalStateException("Usuario autenticado no encontrado: " + currentUser);
        }

        // Obtener lista de usuarios (excluyendo al usuario actual)
        List<User> users = userRepository.findAllByUsernameNot(currentUser);
        List<ChatMessageDto> messages = List.of();

        if (recipient != null) {
            messages = chatService.getChatMessages(sender.get().getUsername(), recipient);
        }

        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("selectedRecipient", recipient);
        model.addAttribute("query", query);
        model.addAttribute("messages", messages);
        return "chat";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterDto registerDto) {
        authService.registerUser(registerDto);
        return "redirect:/chat";
    }
}