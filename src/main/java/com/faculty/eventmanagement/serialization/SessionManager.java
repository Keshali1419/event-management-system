package com.faculty.eventmanagement.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {


    private final ConcurrentHashMap<Long, UserSession> activeSessions
            = new ConcurrentHashMap<>();

    private static final String SESSION_DIR = "sessions/";


    private final ObjectMapper objectMapper;

    public SessionManager() {
        new File(SESSION_DIR).mkdirs();
        this.objectMapper = new ObjectMapper();

        this.objectMapper.registerModule(new JavaTimeModule());
    }


    public void saveSession(UserSession session) {
        activeSessions.put(session.getUserId(), session);


        String filePath = SESSION_DIR + "session_"
                + session.getUserId() + ".json";
        try {
            objectMapper.writeValue(new File(filePath), session);
            System.out.println("[SESSION] Saved session for: "
                    + session.getFullName());
        } catch (Exception e) {
            System.err.println("[SESSION] Failed to save session: "
                    + e.getMessage());
        }
    }


    public UserSession loadSession(Long userId) {
        if (activeSessions.containsKey(userId)) {
            System.out.println("[SESSION] Loaded from memory for userId: "
                    + userId);
            return activeSessions.get(userId);
        }


        String filePath = SESSION_DIR + "session_" + userId + ".json";
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("[SESSION] No session found for userId: "
                    + userId);
            return null;
        }

        try {
            UserSession session = objectMapper.readValue(file,
                    UserSession.class);

            // Fix 8.2: Reject expired sessions immediately on load
            if (session.isExpired()) {
                System.out.println("[SESSION] Session expired for userId: "
                        + userId);
                invalidateSession(userId);
                return null;
            }

            activeSessions.put(userId, session);
            System.out.println("[SESSION] Loaded from file for: "
                    + session.getFullName());
            return session;
        } catch (Exception e) {
            System.err.println("[SESSION] Failed to load session for userId: "
                    + userId + " — " + e.getMessage());
            return null;
        }
    }


    public void invalidateSession(Long userId) {
        activeSessions.remove(userId);
        // Fix 8.1: delete .json file instead of .ser
        File file = new File(SESSION_DIR + "session_" + userId + ".json");
        if (file.exists()) file.delete();
        System.out.println("[SESSION] Session invalidated for userId: "
                + userId);
    }

    public boolean isSessionActive(Long userId) {
        return activeSessions.containsKey(userId)
                && activeSessions.get(userId).isActive();
    }
}