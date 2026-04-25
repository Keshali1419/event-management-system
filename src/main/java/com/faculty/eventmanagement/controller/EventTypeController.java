package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.EventType;
import com.faculty.eventmanagement.services.EventTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @PostMapping
    public ResponseEntity<?> addEventType(@RequestBody Map<String, String> body) {
        try {
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
    public ResponseEntity<?> updateEventType(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            EventType updated = eventTypeService.updateEventType(id, body.get("name"));
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventType(@PathVariable Long id) {
        try {
            eventTypeService.deleteEventType(id);
            return ResponseEntity.ok("Event type deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}