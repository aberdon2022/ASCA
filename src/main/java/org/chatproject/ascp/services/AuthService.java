package org.chatproject.ascp.services;

import org.chatproject.ascp.dto.UserDto;
import org.chatproject.ascp.models.Role;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.RoleRepository;
import org.chatproject.ascp.repository.UserRepository;
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

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) {
        String username = userDto.getDisplayName() + "_" + UUID.randomUUID().toString();

        Role role = roleRepository.findByAuthority("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = new User(username, userDto.getDisplayName(), userDto.getEmail(), encodedPassword, roles);
        return userRepository.save(user);
    }

    public User loginUser(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

}
