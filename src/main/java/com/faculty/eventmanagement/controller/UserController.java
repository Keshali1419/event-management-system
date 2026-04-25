package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.serialization.UserSession;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody java.util.Map<String, String> credentials) {
        try {
            User user = userService.loginWithCredentials(
                    credentials.get("email"),
                    credentials.get("password")
            );
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/login")
    public ResponseEntity<UserSession> login(@PathVariable Long id) {
        return ResponseEntity.ok(userService.login(id));
    }

    @PostMapping("/{id}/logout")
    public ResponseEntity<String> logout(@PathVariable Long id) {
        userService.logout(id);
        return ResponseEntity.ok("User logged out successfully");
    }

    @GetMapping("/{id}/session")
    public ResponseEntity<UserSession> getSession(@PathVariable Long id) {
        UserSession session = userService.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }
}