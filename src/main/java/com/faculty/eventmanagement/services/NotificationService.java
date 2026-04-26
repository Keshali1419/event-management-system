package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.factory.NotificationFactory;
import com.faculty.eventmanagement.notification.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {


    public void sendNotification(String type, String recipient, String message) {
        Notification notification = NotificationFactory.createNotification(type);
        notification.send(recipient, message);
    }


    public void sendEmail(String recipient, String message) {
        sendNotification("EMAIL", recipient, message);
    }

    public void sendSMS(String recipient, String message) {
        sendNotification("SMS", recipient, message);
    }

    public void sendPush(String recipient, String message) {
        sendNotification("PUSH", recipient, message);
    }
}