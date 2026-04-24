package com.faculty.eventmanagement.factory;

import com.faculty.eventmanagement.notification.EmailNotification;
import com.faculty.eventmanagement.notification.Notification;
import com.faculty.eventmanagement.notification.PushNotification;
import com.faculty.eventmanagement.notification.SMSNotification;

public class NotificationFactory {
    public static Notification createNotification(String type){
        return switch (type.toUpperCase()){
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SMSNotification();
            case "PUSH" -> new PushNotification();
            default -> throw  new IllegalArgumentException("Unknown notification type : " + type );
        };
    }
}
