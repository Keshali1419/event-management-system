package com.faculty.eventmanagement.concurrency;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.notification.Notification;
import com.faculty.eventmanagement.factory.NotificationFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncNotificationService {


    @Async("notificationExecutor")
    public CompletableFuture<Void> sendBulkEmailNotifications(
            List<User> users, String message) {

        System.out.println("[ASYNC] Starting bulk email on thread: "
                + Thread.currentThread().getName());

        users.forEach(user -> {
            Notification notification = NotificationFactory
                    .createNotification("EMAIL");
            notification.send(user.getEmail(), message);


            try { Thread.sleep(100); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("[ASYNC] Bulk email complete on thread: "
                + Thread.currentThread().getName());

        return CompletableFuture.completedFuture(null);
    }

    @Async("notificationExecutor")
    public CompletableFuture<Void> sendEventReminders(Event event,
                                                      List<User> attendees) {

        System.out.println("[ASYNC] Sending reminders for: "
                + event.getTitle()
                + " on thread: " + Thread.currentThread().getName());

        attendees.forEach(user -> {
            Notification notification = NotificationFactory
                    .createNotification("PUSH");
            notification.send(user.getEmail(),
                    "Reminder: " + event.getTitle()
                            + " is coming up soon!");
        });

        return CompletableFuture.completedFuture(null);
    }
}