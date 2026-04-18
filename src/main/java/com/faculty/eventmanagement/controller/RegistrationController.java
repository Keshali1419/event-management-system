package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<Registration> register(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(
                registrationService.registerUserForEvent(userId, eventId));
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