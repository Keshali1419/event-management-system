package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.adapter.ExternalEmailAdapter;
import com.faculty.eventmanagement.adapter.ExternalEmailService;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.model.UserRole;
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
        if (user == null) {
            throw new IllegalArgumentException("User details are required.");
        }

        String fullName = user.getFullName() == null ? "" : user.getFullName().trim();
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        String password = user.getPassword() == null ? "" : user.getPassword();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Full name, email and password are required.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account already exists for this email.");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        if (user.getRole() == null) {
            user.setRole(UserRole.STUDENT);
        }

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
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("Email and password are required");
        }

        User user = userRepository.findByEmail(normalizedEmail)
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