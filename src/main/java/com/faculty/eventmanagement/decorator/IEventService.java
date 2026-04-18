package com.faculty.eventmanagement.decorator;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.EventStatus;

import java.util.List;

public interface IEventService {
    Event createEvent(Event event);
    Event updateEventStatus(Long id, EventStatus newStatus);
    List<Event> getAllEvents();
    Event getEventById(Long id);
}
