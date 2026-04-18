package com.faculty.eventmanagement.serialization;

import com.faculty.eventmanagement.model.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;

public class UserSession implements Serializable {

    // serialVersionUID tracks class version changes
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String fullName;
    private String email;
    private UserRole role;
    private LocalDateTime loginTime;
    private boolean isActive;

    public UserSession(Long userId, String fullName,
                       String email, UserRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.loginTime = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "UserSession{userId=" + userId
                + ", fullName=" + fullName
                + ", role=" + role
                + ", loginTime=" + loginTime
                + ", isActive=" + isActive + "}";
    }
}