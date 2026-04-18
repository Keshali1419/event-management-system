package com.faculty.eventmanagement.adapter;

public class ExternalEmailService {

    public void sendEmailMessage(String toAddress, String subject, String body) {
        System.out.println("[EXTERNAL EMAIL SERVICE]" + " To: " + toAddress + " | Subject: " + subject + " | Body: " + body);
    }
    public boolean verifyEmailAddress(String email) {
        return email != null && email.contains("@");
    }
}
