package com.faculty.eventmanagement.serialization;

import com.faculty.eventmanagement.model.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;


public class UserSession {

    private Long userId;
    private String fullName;
    private String email;
    private UserRole role;
    private LocalDateTime loginTime;
    private boolean isActive;


    @JsonCreator
    public UserSession(
            @JsonProperty("userId")    Long userId,
            @JsonProperty("fullName")  String fullName,
            @JsonProperty("email")     String email,
            @JsonProperty("role")      UserRole role) {
        this.userId    = userId;
        this.fullName  = fullName;
        this.email     = email;
        this.role      = role;
        this.loginTime = LocalDateTime.now();
        this.isActive  = true;
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(loginTime.plusHours(24));
    }


    public Long getUserId()            { return userId; }
    public String getFullName()        { return fullName; }
    public String getEmail()           { return email; }
    public UserRole getRole()          { return role; }
    public LocalDateTime getLoginTime(){ return loginTime; }
    public boolean isActive()          { return isActive; }
    public void setActive(boolean active) { isActive = active; }


    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
    public void setUserId(Long userId)      { this.userId = userId; }
    public void setFullName(String name)    { this.fullName = name; }
    public void setEmail(String email)      { this.email = email; }
    public void setRole(UserRole role)      { this.role = role; }

    @Override
    public String toString() {
        return "UserSession{userId=" + userId
                + ", fullName=" + fullName
                + ", role=" + role
                + ", loginTime=" + loginTime
                + ", isActive=" + isActive + "}";
    }
}