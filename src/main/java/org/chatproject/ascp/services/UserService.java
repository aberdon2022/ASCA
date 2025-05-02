package org.chatproject.ascp.services;

import org.chatproject.ascp.dto.UserDto;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(UserDto userDto) {
        String cleanedDisplayName = Jsoup.clean(userDto.getDisplayName(), Safelist.simpleText());
        String username = cleanedDisplayName + UUID.randomUUID().toString();
        User user = new User(username, userDto.getDisplayName(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }

    public void loginUser(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
    }
}
