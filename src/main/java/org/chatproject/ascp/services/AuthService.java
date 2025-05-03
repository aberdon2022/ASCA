package org.chatproject.ascp.services;

import org.chatproject.ascp.dto.AuthResponseDto;
import org.chatproject.ascp.dto.UserDto;
import org.chatproject.ascp.models.Role;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.RoleRepository;
import org.chatproject.ascp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthResponseDto registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return new AuthResponseDto("Email already registered");
        }

        String username = userDto.getDisplayName() + "@" + UUID.randomUUID().toString();

        Role role = roleRepository.findByAuthority("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = new User(username, userDto.getDisplayName(), userDto.getEmail(), encodedPassword, roles);
        userRepository.save(user);

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
            );
            String token = tokenService.generateJwt(auth);
            return new AuthResponseDto(user, token);
        } catch (AuthenticationException e) {
            return new AuthResponseDto("Failed to authenticate after registration");
        }
    }

    public AuthResponseDto loginUser(UserDto userDto) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
            String token = tokenService.generateJwt(auth);
            User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            return new AuthResponseDto(user, token);
        } catch (AuthenticationException e) {
            return new AuthResponseDto("Invalid username or password");
        }
    }
}
