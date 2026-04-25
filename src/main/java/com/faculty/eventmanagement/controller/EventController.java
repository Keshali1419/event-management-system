package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.faculty.eventmanagement.model.EventStatus;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
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
    public ResponseEntity<Event> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(eventService.updateEventStatus(id, eventStatus));
    }
}