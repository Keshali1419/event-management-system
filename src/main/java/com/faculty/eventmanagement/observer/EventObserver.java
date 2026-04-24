package com.faculty.eventmanagement.observer;

import com.faculty.eventmanagement.model.Event;

public interface EventObserver {
    void update(Event event, String action);
}