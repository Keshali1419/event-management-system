package com.faculty.eventmanagement.template;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.model.RegistrationStatus;
import com.faculty.eventmanagement.model.User;

import java.time.LocalDateTime;

public abstract class EventRegistrationTemplate {

    // Template method — final so nobody can change the order of steps
    public final Registration register(User user, Event event) {
        validateUser(user);
        checkEventAvailability(event);
        Registration registration = processRegistration(user, event);
        sendConfirmation(user, event);
        return registration;
    }

    // Step 1 — subclasses must implement this
    protected abstract void validateUser(User user);

    // Step 2 — common for all, already implemented
    protected void checkEventAvailability(Event event) {
        if (event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new RuntimeException("Sorry, event is fully booked: "
                    + event.getTitle());
        }
        System.out.println("[TEMPLATE] Availability check passed for: "
                + event.getTitle());
    }

    // Step 3 — subclasses must implement this
    protected abstract Registration processRegistration(User user, Event event);

    // Step 4 — subclasses must implement this
    protected abstract void sendConfirmation(User user, Event event);

    // Helper method to build registration object
    protected Registration buildRegistration(User user, Event event) {
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegisteredAt(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.CONFIRMED);
        return registration;
    }
}