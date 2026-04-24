package com.faculty.eventmanagement.factory;

import com.faculty.eventmanagement.notification.EmailNotification;
import com.faculty.eventmanagement.notification.Notification;

public class AcademicEventNotificationFactory implements EventNotificationAbstractFactory {
    @Override
    public Notification createRegistrationNotification() {
        return new EmailNotification();
    }

    @Override
    public Notification createReminderNotification() {
        return new EmailNotification();
    }

    @Override
    public Notification createCancellationNotification() {
        return new EmailNotification();
    }
}
