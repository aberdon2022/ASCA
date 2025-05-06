package org.chatproject.ascp.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping("/current")
    public ResponseEntity<String> getCurrentUser(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(username);
    }
}
