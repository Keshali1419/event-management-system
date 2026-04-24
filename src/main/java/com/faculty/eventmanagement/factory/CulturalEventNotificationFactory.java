package com.faculty.eventmanagement.factory;

import com.faculty.eventmanagement.notification.Notification;
import com.faculty.eventmanagement.notification.PushNotification;
import com.faculty.eventmanagement.notification.SMSNotification;

public class CulturalEventNotificationFactory implements EventNotificationAbstractFactory{
    @Override
    public Notification createRegistrationNotification() {
        return new SMSNotification();
    }

    @Override
    public Notification createReminderNotification() {
        return new PushNotification();
    }

    @Override
    public Notification createCancellationNotification() {
        return new SMSNotification();
    }
}
