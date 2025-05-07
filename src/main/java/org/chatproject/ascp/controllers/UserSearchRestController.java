package org.chatproject.ascp.controllers;

import lombok.AllArgsConstructor;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.UserRepository;
import org.chatproject.ascp.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserSearchRestController {

    private final UserRepository userRepository;
    private final ChatService chatService;

    @PostMapping("/search-users/json")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam(value = "query", required = false) String query) {

        // Get current authenticated user
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        // Search users based on query (excluding current user)
        List<User> users = query == null || query.trim().isEmpty()
                ? userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(currentUser))
                .collect(Collectors.toList())
                : userRepository.findByUsernameContainingIgnoreCase(query).stream()
                .filter(user -> !user.getUsername().equals(currentUser))
                .collect(Collectors.toList());

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);

        return ResponseEntity.ok(response);
    }
}