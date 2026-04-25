package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.model.EventType;
import com.faculty.eventmanagement.repository.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    public EventType addEventType(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Event type name is required.");
        }
        if (eventTypeRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("This event type already exists.");
        }

        EventType eventType = new EventType();
        eventType.setName(name);
        return eventTypeRepository.save(eventType);
    }

    public List<EventType> getAllEventTypes() {
        return eventTypeRepository.findAllByOrderByNameAsc();
    }

    public EventType updateEventType(Long id, String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Event type name is required.");
        }

        EventType existing = eventTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event type not found."));

        eventTypeRepository.findByNameIgnoreCase(name)
                .filter(type -> !type.getId().equals(id))
                .ifPresent(type -> {
                    throw new IllegalArgumentException("This event type already exists.");
                });

        existing.setName(name);
        return eventTypeRepository.save(existing);
    }

    public void deleteEventType(Long id) {
        if (!eventTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Event type not found.");
        }
        eventTypeRepository.deleteById(id);
    }
}