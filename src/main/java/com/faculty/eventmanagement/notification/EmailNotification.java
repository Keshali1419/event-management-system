package com.faculty.eventmanagement.notification;

public class EmailNotification implements Notification {

    @Override
    public void send(String recipient, String message) {

        System.out.printf("[EMAIL] TO: %s | MESSAGE: %s%n",
                recipient, message);
    }

    @Override
    public String getType() {
        return "EMAIL";
    }
}