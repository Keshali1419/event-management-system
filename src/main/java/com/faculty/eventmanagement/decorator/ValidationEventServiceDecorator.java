package com.faculty.eventmanagement.decorator;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.EventStatus;

public class ValidationEventServiceDecorator extends EventServiceDecorator{

    public ValidationEventServiceDecorator(IEventService wrapped) {
        super(wrapped);
    }

    @Override
    public Event createEvent(Event event) {
        if(event.getTitle() == null || event.getTitle().isBlank()){
            throw new IllegalArgumentException("Event title cannot be empty");
        }
        if(event.getLocation() == null || event.getLocation().isBlank()){
            throw new IllegalArgumentException("Event location cannot be empty");
        }
        if(event.getEventDate() == null){
            throw new IllegalArgumentException("Event date cannot null");
        }
        if (event.getMaxAttendees() <= 0) {
            throw new IllegalArgumentException("Event max attendees must be greater than 0");
        }

        System.out.println("[VALIDATION] All fields valid for event: " +  event.getTitle());
        return wrapped.createEvent(event);
    }

    @Override
    public Event updateEventStatus(Long id, EventStatus newStatus) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        System.out.println("[VALIDATION] Status update request valid for ID: " +  id);
        return wrapped.updateEventStatus(id, newStatus);
    }
}
