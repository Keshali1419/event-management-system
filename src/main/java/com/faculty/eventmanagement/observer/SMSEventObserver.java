package com.faculty.eventmanagement.observer;

import com.faculty.eventmanagement.model.Event;
import org.springframework.stereotype.Component;

@Component
public class SMSEventObserver implements EventObserver {

    @Override
    public void update(Event event, String action) {
        System.out.println("[SMS OBSERVER] Action: " + action
                + " | Event: " + event.getTitle()
                + " | Sending SMS to all registered attendees...");
    }
}