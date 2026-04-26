package com.faculty.eventmanagement.decorator;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.EventStatus;
import java.util.List;

public abstract class EventServiceDecorator implements IEventService {

    protected  final IEventService wrapped;

    public EventServiceDecorator(IEventService wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Event createEvent(Event event) {
        return wrapped.createEvent(event);
    }

    @Override
    public Event updateEventStatus(Long id, EventStatus newStatus) {
        return wrapped.updateEventStatus(id, newStatus);
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent) {
        return wrapped.updateEvent(id, updatedEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        wrapped.deleteEvent(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return wrapped.getAllEvents();
    }

    @Override
    public Event getEventById(Long id){
        return wrapped.getEventById(id);
    }

}
