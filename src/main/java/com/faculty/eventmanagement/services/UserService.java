package com.faculty.eventmanagement.service;

import com.faculty.eventmanagement.adapter.ExternalEmailAdapter;
import com.faculty.eventmanagement.adapter.ExternalEmailService;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.repository.UserRepository;
import com.faculty.eventmanagement.serialization.SessionManager;
import com.faculty.eventmanagement.serialization.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public User createUser(User user) {
        User saved = userRepository.save(user);

        // Adapter pattern — using external email service
        // through our Notification interface
        ExternalEmailAdapter emailAdapter = new ExternalEmailAdapter(
                new ExternalEmailService());
        emailAdapter.send(saved.getEmail(),
                "Welcome to Faculty Event System, "
                        + saved.getFullName() + "!");

        return saved;
    }

    // Simulate login — creates and serializes a session
    public UserSession login(Long userId) {
        User user = getUserById(userId);
        UserSession session = new UserSession(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
        sessionManager.saveSession(session);
        return session;
    }

    public User loginWithCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        // Create and save session using our Serialization pattern
        UserSession session = new UserSession(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
        sessionManager.saveSession(session);

        return user;
    }

    // Simulate logout — invalidates session
    public void logout(Long userId) {
        sessionManager.invalidateSession(userId);
    }

    public UserSession getSession(Long userId) {
        return sessionManager.loadSession(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + id));
    }
}