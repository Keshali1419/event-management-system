package com.faculty.eventmanagement.factory;

import com.faculty.eventmanagement.notification.Notification;

public interface EventNotificationAbstractFactory {
    Notification createRegistrationNotification();
    Notification createReminderNotification();
    Notification createCancellationNotification();
}
