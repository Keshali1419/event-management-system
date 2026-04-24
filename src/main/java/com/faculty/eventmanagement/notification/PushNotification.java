package com.faculty.eventmanagement.notification;

public class PushNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        System.out.printf("[PUSH] To :" + recipient + " | Message: " + message);

    }

    @Override
    public String getType() {
        return "PUSH";
    }
}
