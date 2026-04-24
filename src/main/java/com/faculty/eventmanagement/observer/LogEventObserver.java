package com.faculty.eventmanagement.observer;

import com.faculty.eventmanagement.model.Event;
import org.springframework.stereotype.Component;

@Component
public class LogEventObserver implements EventObserver {

    @Override
    public void update(Event event, String action) {
        System.out.println("[LOG OBSERVER] Action: " + action
                + " | Event: " + event.getTitle()
                + " | Status: " + event.getStatus()
                + " | Attendees: " + event.getCurrentAttendees()
                + "/" + event.getMaxAttendees());
    }
}