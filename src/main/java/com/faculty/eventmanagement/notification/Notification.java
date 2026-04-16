package com.faculty.eventmanagement.notification;

public interface Notification {
    void send(String recipient, String message);
    String getType();
}
