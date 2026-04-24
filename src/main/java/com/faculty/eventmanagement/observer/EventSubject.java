package com.faculty.eventmanagement.observer;

public interface EventSubject {
    void addObserver(EventObserver observer);
    void removeObserver(EventObserver observer);
    void notifyObservers(com.faculty.eventmanagement.model.Event event, String action);
}