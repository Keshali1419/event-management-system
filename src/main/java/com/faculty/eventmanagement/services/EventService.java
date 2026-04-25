package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.config.EventConfigManager;
import com.faculty.eventmanagement.decorator.IEventService;
import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.EventStatus;
import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.observer.EventObserver;
import com.faculty.eventmanagement.observer.EventSubject;
import com.faculty.eventmanagement.repository.EventRepository;
import com.faculty.eventmanagement.repository.RegistrationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.faculty.eventmanagement.observer.EmailEventObserver;
import com.faculty.eventmanagement.observer.SMSEventObserver;
import com.faculty.eventmanagement.observer.LogEventObserver;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService implements EventSubject, IEventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    // Observer pattern — list of all observers
    private final List<EventObserver> observers = new ArrayList<>();

    // Inject all observers automatically
    private final EmailEventObserver emailEventObserver;
    private final SMSEventObserver smsEventObserver;
    private final LogEventObserver logEventObserver;

    // Register all observers when service starts
    @PostConstruct
    public void initObservers() {
        addObserver(emailEventObserver);
        addObserver(smsEventObserver);
        addObserver(logEventObserver);
    }

    @Override
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Event event, String action) {
        observers.forEach(observer -> observer.update(event, action));
    }

    public Event createEvent(Event event) {
        validateEvent(event);
        EventConfigManager config = EventConfigManager.getInstance();

        if (event.getMaxAttendees() > config.getMaxAttendeesPerEvent()) {
            event.setMaxAttendees(config.getMaxAttendeesPerEvent());
        }

        event.setStatus(EventStatus.UPCOMING);
        event.setCurrentAttendees(0);
        Event saved = eventRepository.save(event);

        // Notify all observers that a new event was created
        notifyObservers(saved, "EVENT_CREATED");

        notificationService.sendEmail(
                config.getAdminEmail(),
                "New event created: " + saved.getTitle()
        );

        return saved;
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent) {
        validateEvent(updatedEvent);

        Event existing = getEventById(id);

        if (updatedEvent.getMaxAttendees() < existing.getCurrentAttendees()) {
            throw new IllegalArgumentException("Max attendees cannot be less than current attendees.");
        }

        EventConfigManager config = EventConfigManager.getInstance();
        int maxAttendees = Math.min(updatedEvent.getMaxAttendees(), config.getMaxAttendeesPerEvent());

        existing.setTitle(updatedEvent.getTitle().trim());
        existing.setDescription(updatedEvent.getDescription());
        existing.setLocation(updatedEvent.getLocation().trim());
        existing.setEventDate(updatedEvent.getEventDate());
        existing.setEventType(updatedEvent.getEventType());
        existing.setImageUrl(updatedEvent.getImageUrl());
        existing.setMaxAttendees(maxAttendees);
        if (updatedEvent.getStatus() != null) {
            existing.setStatus(updatedEvent.getStatus());
        }

        Event updated = eventRepository.save(existing);
        notifyObservers(updated, "EVENT_UPDATED");
        return updated;
    }

    public Event updateEventStatus(Long id, EventStatus newStatus) {
        Event event = getEventById(id);
        event.setStatus(newStatus);
        Event updated = eventRepository.save(event);

        // Notify all observers that status changed
        notifyObservers(updated, "STATUS_CHANGED");

        return updated;
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        List<Registration> registrations = registrationRepository.findByEventId(id);
        if (!registrations.isEmpty()) {
            registrationRepository.deleteAll(registrations);
        }

        notifyObservers(event, "EVENT_DELETED");
        eventRepository.delete(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event details are required.");
        }

        String title = event.getTitle() == null ? "" : event.getTitle().trim();
        String location = event.getLocation() == null ? "" : event.getLocation().trim();

        if (title.isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be empty");
        }
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Event location cannot be empty");
        }
        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date cannot be empty");
        }
        if (event.getMaxAttendees() <= 0) {
            throw new IllegalArgumentException("Event max attendees must be greater than 0");
        }
    }
}