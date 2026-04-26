package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.services.EventService;
import com.faculty.eventmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEvent(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam String eventDate,
            @RequestParam(required = false) String eventType,
            @RequestParam int maxAttendees,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam Long actorUserId) throws Exception {
        ResponseEntity<String> authResponse = ensureAdmin(actorUserId);
        if (authResponse != null) {
            return authResponse;
        }

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventDate(LocalDateTime.parse(eventDate));
        event.setEventType(eventType);
        event.setMaxAttendees(maxAttendees);

        if (image != null && !image.isEmpty()) {
            event.setImageUrl(saveEventImage(image));
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam String eventDate,
            @RequestParam(required = false) String eventType,
            @RequestParam int maxAttendees,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam Long actorUserId) throws Exception {
        ResponseEntity<String> authResponse = ensureAdmin(actorUserId);
        if (authResponse != null) {
            return authResponse;
        }

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventDate(LocalDateTime.parse(eventDate));
        event.setEventType(eventType);
        event.setMaxAttendees(maxAttendees);

        if (image != null && !image.isEmpty()) {
            event.setImageUrl(saveEventImage(image));
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

    private String saveEventImage(MultipartFile image) throws Exception {
        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);

        String originalName = image.getOriginalFilename() == null ? "event-image" : image.getOriginalFilename();
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String storedFileName = UUID.randomUUID() + "_" + safeName;
        Path targetPath = uploadDir.resolve(storedFileName);

        Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + storedFileName;
    }
}