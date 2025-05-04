package org.chatproject.ascp.services;

import org.chatproject.ascp.dto.UserSearchDto;
import org.chatproject.ascp.models.User;
import org.chatproject.ascp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities());
    }

    public List<UserSearchDto> getUsersByDisplayName(String displayName) {
        return userRepository.findByDisplayNameContaining(displayName).stream()
                .map(user -> new UserSearchDto(user.getUsername(), user.getDisplayName()))
                .collect(Collectors.toList());
    }
}
