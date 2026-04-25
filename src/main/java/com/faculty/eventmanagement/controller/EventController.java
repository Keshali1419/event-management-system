package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.services.EventService;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.faculty.eventmanagement.model.EventStatus;
import com.faculty.eventmanagement.model.UserRole;
import com.faculty.eventmanagement.serialization.UserSession;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody Event event,
            @RequestParam Long actorUserId) {
        ResponseEntity<String> authResponse = ensureAdmin(actorUserId);
        if (authResponse != null) {
            return authResponse;
        }
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Long actorUserId,
            @RequestParam String status) {
        ResponseEntity<String> authResponse = ensureAdmin(actorUserId);
        if (authResponse != null) {
            return authResponse;
        }
        EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(eventService.updateEventStatus(id, eventStatus));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody Event event,
            @RequestParam Long actorUserId) {
        ResponseEntity<String> authResponse = ensureAdmin(actorUserId);
        if (authResponse != null) {
            return authResponse;
        }
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id,
            @RequestParam Long actorUserId) {
        ResponseEntity<String> authResponse = ensureAdmin(actorUserId);
        if (authResponse != null) {
            return authResponse;
        }
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted successfully");
    }

    private ResponseEntity<String> ensureAdmin(Long actorUserId) {
        UserSession session = userService.getSession(actorUserId);
        if (session == null || !session.isActive()) {
            return ResponseEntity.status(401).body("Please login first.");
        }
        if (session.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).body("Only Admin can modify events.");
        }
        return null;
    }
}