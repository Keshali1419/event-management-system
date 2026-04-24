package com.faculty.eventmanagement.template;

import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.Registration;
import com.faculty.eventmanagement.model.User;

public class StandardEventRegistration extends EventRegistrationTemplate {

    @Override
    protected void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email is required");
        }
        System.out.println("[TEMPLATE] User validation passed for: "
                + user.getFullName());
    }

    @Override
    protected Registration processRegistration(User user, Event event) {
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        System.out.println("[TEMPLATE] Registration processed for: "
                + user.getFullName() + " → " + event.getTitle());
        return buildRegistration(user, event);
    }

    @Override
    protected void sendConfirmation(User user, Event event) {
        System.out.println("[TEMPLATE] Confirmation sent to: "
                + user.getEmail() + " for event: " + event.getTitle());
    }
}