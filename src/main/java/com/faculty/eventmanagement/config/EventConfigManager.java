package com.faculty.eventmanagement.config;

public class EventConfigManager {

    private static EventConfigManager instance;

    private String systemName;
    private int maxAttendeesPerEvent;
    private String adminEmail;

    private EventConfigManager() {
        this.systemName = "Faculty Event Management System";
        this.maxAttendeesPerEvent = 500;
        this.adminEmail = "admin@faculty.edu";
    }

    public static synchronized EventConfigManager getInstance() {
        if (instance == null) {
            instance = new EventConfigManager();
        }
        return instance;
    }

    public String getSystemName() {
        return systemName;
    }
    public int getMaxAttendeesPerEvent() {
        return maxAttendeesPerEvent;
    }
    public String getAdminEmail() {
        return adminEmail;
    }
}