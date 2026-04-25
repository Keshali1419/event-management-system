package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.EventType;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.model.UserRole;
import com.faculty.eventmanagement.services.EventTypeService;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
public class EventTypeController {

    private final EventTypeService eventTypeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> addEventType(
            @RequestHeader("X-User-Id") Long actorUserId,
            @RequestBody Map<String, String> body) {
        try {
            requireAdmin(actorUserId);
            EventType created = eventTypeService.addEventType(body.get("name"));
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EventType>> getAllEventTypes() {
        return ResponseEntity.ok(eventTypeService.getAllEventTypes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEventType(
            @RequestHeader("X-User-Id") Long actorUserId,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            requireAdmin(actorUserId);
            EventType updated = eventTypeService.updateEventType(id, body.get("name"));
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventType(
            @RequestHeader("X-User-Id") Long actorUserId,
            @PathVariable Long id) {
        try {
            requireAdmin(actorUserId);
            eventTypeService.deleteEventType(id);
            return ResponseEntity.ok("Event type deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void requireAdmin(Long actorUserId) {
        User actor;
        try {
            actor = userService.getUserById(actorUserId);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        if (actor.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can manage event types.");
        }
    }
}