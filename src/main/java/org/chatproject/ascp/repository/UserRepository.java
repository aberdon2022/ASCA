package org.chatproject.ascp.repository;

import org.chatproject.ascp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByDisplayNameContaining(String displayName);
    Optional<User> findByUsername(String username);
}