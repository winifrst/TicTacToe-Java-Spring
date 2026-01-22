package org.tictactoe.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.UserService;
import org.tictactoe.web.model.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable UUID userId) {
        return userService.findById(userId)
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setUsername(user.getUsername());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        try {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            return getUserInfo(userId);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}