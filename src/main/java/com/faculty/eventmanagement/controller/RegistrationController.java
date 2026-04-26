package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.serialization.UserSession;
import com.faculty.eventmanagement.services.RegistrationService;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> register(
            @RequestParam Long userId,
            @RequestParam Long eventId) {

        UserSession session = userService.getSession(userId);
        if (session == null || !session.isActive()) {
            return ResponseEntity.status(401).body("Please login first.");
        }
        if (!session.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("You can only register yourself if you are logged in.");
        }
        try {
            return ResponseEntity.ok(
                    registrationService.registerUserForEvent(userId, eventId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Registration>> getByEvent(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(
                registrationService.getRegistrationsByEvent(eventId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Registration>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                registrationService.getRegistrationsByUser(userId));
    }
}