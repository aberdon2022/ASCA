    package org.chatproject.ascp.services;

    import org.chatproject.ascp.dto.ApiAuthResponseDto;
    import org.chatproject.ascp.dto.LoginDto;
    import org.chatproject.ascp.dto.RegisterDto;
    import org.chatproject.ascp.dto.RegisterResponseDto;
    import org.chatproject.ascp.exceptions.UsernameAlreadyRegisteredException;
    import org.chatproject.ascp.exceptions.InvalidCredentialsException;
    import org.chatproject.ascp.models.Role;
    import org.chatproject.ascp.models.User;
    import org.chatproject.ascp.repository.RoleRepository;
    import org.chatproject.ascp.repository.UserRepository;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.HashSet;
    import java.util.Set;

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

        public RegisterResponseDto registerUser(RegisterDto registerDto) {
            if (userRepository.findByUsername(registerDto.username()).isPresent()) {
                throw new UsernameAlreadyRegisteredException("Username already registered");
            }

            Role role = roleRepository.findByAuthority("USER").orElseThrow(() -> new RuntimeException("Role not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(role);

            String encodedPassword = passwordEncoder.encode(registerDto.password());
            User user = new User(registerDto.username(), encodedPassword, roles);
            userRepository.save(user);
            return new RegisterResponseDto(user);
        }

        public void loginWeb(LoginDto loginDto) {
            try {
                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
                SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(auth));
            } catch (AuthenticationException e) {
                throw new InvalidCredentialsException("Invalid username or password");
            }
        }

        public ApiAuthResponseDto loginApiUser(LoginDto loginDto) {
            try {
                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
                String token = tokenService.generateJwt(auth);
                User user = userRepository.findByUsername(loginDto.username()).orElseThrow(() -> new RuntimeException("User not found"));
                return new ApiAuthResponseDto(user, token);
            } catch (AuthenticationException e) {
                throw  new InvalidCredentialsException("Invalid credentials");
            }
        }
    }
