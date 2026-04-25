package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.concurrency.AsyncNotificationService;
import com.faculty.eventmanagement.concurrency.ThreadSafeRegistrationService;
import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserService userService;
    private final com.faculty.eventmanagement.services.EventService eventService;
    private final ThreadSafeRegistrationService threadSafeService;
    private final AsyncNotificationService asyncNotificationService;

    public Registration registerUserForEvent(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        // Thread safe registration using ReentrantLock
        Registration registration = threadSafeService
                .registerSafely(user, event);

        // Send confirmation async — user doesn't wait for this
        asyncNotificationService.sendBulkEmailNotifications(
                List.of(user),
                "You have successfully registered for: "
                        + event.getTitle()
        );

        return registration;
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<Registration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }
}