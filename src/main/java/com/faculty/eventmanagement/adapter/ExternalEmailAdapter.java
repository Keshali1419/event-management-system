package com.faculty.eventmanagement.adapter;

import com.faculty.eventmanagement.notification.Notification;

public class ExternalEmailAdapter implements Notification {

    private final ExternalEmailService externalEmailService;

    public ExternalEmailAdapter(ExternalEmailService externalEmailService) {
        this.externalEmailService = externalEmailService;
    }

    @Override
    public void send(String recipient, String message) {
        if(!externalEmailService.verifyEmailAddress(recipient)){
            throw new IllegalArgumentException("Invalid email address: " + recipient);
        }

        externalEmailService.sendEmailMessage(
                recipient,
                "Faculty Event System Notification",
                message
        );

    }

    @Override
    public String getType() {
        return "EXTERNAL_EMAIL";
    }
}
