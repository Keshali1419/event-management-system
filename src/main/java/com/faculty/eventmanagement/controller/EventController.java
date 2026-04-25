package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.decorator.IEventService;
import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.model.UserRole;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.faculty.eventmanagement.model.EventStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestHeader("X-User-Id") Long actorUserId,
            @RequestBody Event event) {
        try {
            requireAdmin(actorUserId);
            return ResponseEntity.ok(eventService.createEvent(event));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @RequestHeader("X-User-Id") Long actorUserId,
            @PathVariable Long id,
            @RequestBody Event event) {
        try {
            requireAdmin(actorUserId);
            return ResponseEntity.ok(eventService.updateEvent(id, event));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity<Event> updateStatus(
            @RequestHeader("X-User-Id") Long actorUserId,
            @PathVariable Long id,
            @RequestParam String status) {
        requireAdmin(actorUserId);
        EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(eventService.updateEventStatus(id, eventStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @RequestHeader("X-User-Id") Long actorUserId,
            @PathVariable Long id) {
        try {
            requireAdmin(actorUserId);
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can manage events.");
        }
    }
}