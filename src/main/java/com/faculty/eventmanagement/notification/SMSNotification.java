package com.faculty.eventmanagement.notification;

public class SMSNotification implements Notification{
    @Override
    public void send(String recipient, String message) {
        System.out.printf("[SMS] To : " + recipient + " | Message : " + message);
    }

    @Override
    public String getType() {
        return "SMS";
    }
}
