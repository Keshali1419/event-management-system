package com.faculty.eventmanagement.observer;

import com.faculty.eventmanagement.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EmailEventObserver implements EventObserver {

    @Override
    public void update(Event event, String action) {
        System.out.println("[EMAIL OBSERVER] Action: " + action
                + " | Event: " + event.getTitle()
                + " | Sending email notification to all registered attendees...");
    }
}