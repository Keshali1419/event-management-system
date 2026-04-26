package com.faculty.eventmanagement.decorator;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.EventStatus;

import java.util.List;

public interface IEventService {
    Event createEvent(Event event);
    Event updateEventStatus(Long id, EventStatus newStatus);
    Event updateEvent(Long id, Event updatedEvent);
    void deleteEvent(Long id);
    List<Event> getAllEvents();
    Event getEventById(Long id);
}
