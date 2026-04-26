package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.model.UserRole;
import com.faculty.eventmanagement.serialization.UserSession;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            if (user.getRole() == UserRole.ADMIN && userService.hasAdminAccount()) {
                return ResponseEntity.badRequest().body("Admin sign up is allowed only when no admin account exists.");
            }
            if (user.getRegistrationNo() == null || user.getRegistrationNo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Registration no is required for sign up.");
            }
            return ResponseEntity.ok(userService.createUser(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam(required = false) String state) {
        userService.deleteUser(id, state);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
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