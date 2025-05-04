package org.chatproject.ascp.services;

import org.chatproject.ascp.dto.AuthResponseDto;
import org.chatproject.ascp.dto.LoginDto;
import org.chatproject.ascp.dto.RegisterDto;
import org.chatproject.ascp.exceptions.EmailAlreadyRegisteredException;
import org.chatproject.ascp.exceptions.InvalidCredentialsException;
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

    public AuthResponseDto registerUser(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        String username = registerDto.displayName() + "@" + UUID.randomUUID();
        Role role = roleRepository.findByAuthority("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        String encodedPassword = passwordEncoder.encode(registerDto.password());
        User user = new User(username, registerDto.displayName(), registerDto.email(), encodedPassword, roles);
        userRepository.save(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(registerDto.email(), registerDto.password());
        String token = tokenService.generateJwt(auth);
        return new AuthResponseDto(user, token);
    }

    public AuthResponseDto loginUser(LoginDto loginDto) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
            String token = tokenService.generateJwt(auth);
            User user = userRepository.findByEmail(loginDto.email()).orElseThrow(() -> new RuntimeException("User not found"));
            return new AuthResponseDto(user, token);
        } catch (AuthenticationException e) {
            throw  new InvalidCredentialsException("Invalid credentials");
        }
    }
}
