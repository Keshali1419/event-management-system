package com.faculty.eventmanagement.decorator;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public class LoggingEventServiceDecorator extends EventServiceDecorator{
    public LoggingEventServiceDecorator(IEventService wrapped) {
        super(wrapped);
    }

    @Override
    public Event createEvent(Event event) {
        System.out.println("[LOG - " + LocalDateTime.now() + "] Creating event: " + event.getTitle());
        Event result = wrapped.createEvent(event);
        System.out.println("[LOG - " + LocalDateTime.now() + "] Event created with ID: " + result.getId());
        return result;
    }

    @Override
    public Event updateEvent(Long id, Event event) {
        System.out.println("[LOG - " + LocalDateTime.now() + "] Updating event ID: " + id);
        Event result = wrapped.updateEvent(id, event);
        System.out.println("[LOG - " + LocalDateTime.now() + "] Event updated successfully");
        return result;
    }

    @Override
    public Event updateEventStatus(Long id, EventStatus newStatus) {
        System.out.println("[LOG - " + LocalDateTime.now() + "] Updating event ID : " + id + " to status: " + newStatus);
        Event result = wrapped.updateEventStatus(id, newStatus);
        System.out.println("[LOG - " + LocalDateTime.now() + "] Event updated successfully ");
        return result;
    }

    @Override
    public void deleteEvent(Long id) {
        System.out.println("[LOG - " + LocalDateTime.now() + "] Deleting event ID: " + id);
        wrapped.deleteEvent(id);
        System.out.println("[LOG - " + LocalDateTime.now() + "] Event deleted successfully");
    }

    @Override
    public List<Event> getAllEvents() {
        System.out.println("[LOG - " + LocalDateTime.now() + "] Fetching all events");
        return wrapped.getAllEvents();
    }
}
