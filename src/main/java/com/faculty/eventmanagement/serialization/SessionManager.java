package com.faculty.eventmanagement.serialization;

import org.springframework.stereotype.Component;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    // In-memory session store — thread safe
    private final ConcurrentHashMap<Long, UserSession> activeSessions
            = new ConcurrentHashMap<>();

    private static final String SESSION_DIR = "sessions/";

    public SessionManager() {
        // Create sessions directory if it doesn't exist
        new File(SESSION_DIR).mkdirs();
    }

    // Save session to memory + serialize to file
    public void saveSession(UserSession session) {
        activeSessions.put(session.getUserId(), session);

        // Serialize to disk
        String filePath = SESSION_DIR + "session_"
                + session.getUserId() + ".ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(session);
            System.out.println("[SESSION] Saved session for: "
                    + session.getFullName());
        } catch (IOException e) {
            System.err.println("[SESSION] Failed to save session: "
                    + e.getMessage());
        }
    }

    // Load session from memory first, then from file
    public UserSession loadSession(Long userId) {
        // Check memory first
        if (activeSessions.containsKey(userId)) {
            System.out.println("[SESSION] Loaded from memory for userId: "
                    + userId);
            return activeSessions.get(userId);
        }

        // Fall back to file
        String filePath = SESSION_DIR + "session_" + userId + ".ser";
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            UserSession session = (UserSession) ois.readObject();
            activeSessions.put(userId, session);
            System.out.println("[SESSION] Loaded from file for: "
                    + session.getFullName());
            return session;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SESSION] No session found for userId: "
                    + userId);
            return null;
        }
    }

    // Invalidate session on logout
    public void invalidateSession(Long userId) {
        activeSessions.remove(userId);
        File file = new File(SESSION_DIR + "session_" + userId + ".ser");
        if (file.exists()) file.delete();
        System.out.println("[SESSION] Session invalidated for userId: "
                + userId);
    }

    public boolean isSessionActive(Long userId) {
        return activeSessions.containsKey(userId)
                && activeSessions.get(userId).isActive();
    }
}