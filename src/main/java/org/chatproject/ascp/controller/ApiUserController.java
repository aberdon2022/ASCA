package org.chatproject.ascp.controller;

import org.chatproject.ascp.dto.UserSearchDto;
import org.chatproject.ascp.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class ApiUserController {
    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<UserSearchDto> searchUsers(@RequestBody UserSearchDto userSearchDto) {
        return userService.getUsersByDisplayName(userSearchDto.displayName());
    }
}
