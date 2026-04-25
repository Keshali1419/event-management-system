package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.adapter.ExternalEmailAdapter;
import com.faculty.eventmanagement.adapter.ExternalEmailService;
import com.faculty.eventmanagement.model.Event;
import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.model.UserRole;
import com.faculty.eventmanagement.model.UserDeletionState;
import com.faculty.eventmanagement.repository.EventRepository;
import com.faculty.eventmanagement.repository.RegistrationRepository;
import com.faculty.eventmanagement.repository.UserRepository;
import com.faculty.eventmanagement.serialization.SessionManager;
import com.faculty.eventmanagement.serialization.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
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
        String registrationNo = user.getRegistrationNo() == null
                ? ""
                : user.getRegistrationNo().trim().toUpperCase();

        if (registrationNo.isBlank()) {
            user.setRegistrationNo(generateRegistrationNo());
        } else {
            if (userRepository.findByRegistrationNo(registrationNo).isPresent()) {
                throw new IllegalArgumentException("This registration number is already in use.");
            }
            user.setRegistrationNo(registrationNo);
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

    public User updateUser(Long userId, User updatedUser) {
        User existing = getUserById(userId);

        String fullName = updatedUser.getFullName() == null ? "" : updatedUser.getFullName().trim();
        String email = updatedUser.getEmail() == null ? "" : updatedUser.getEmail().trim().toLowerCase();
        String phone = updatedUser.getPhone() == null ? "" : updatedUser.getPhone().trim();

        if (fullName.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Full name and email are required.");
        }

        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("An account already exists for this email.");
                });

        existing.setFullName(fullName);
        existing.setEmail(email);
        existing.setPhone(phone.isEmpty() ? null : phone);

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(updatedUser.getPassword());
        }

        return userRepository.save(existing);
    }

    public void deleteUser(Long userId) {
        deleteUser(userId, null);
    }

    public void deleteUser(Long userId, String state) {
        User user = getUserById(userId);
        
        if (state != null && !state.isBlank()) {
            try {
                UserDeletionState deletionState = UserDeletionState.valueOf(state.toUpperCase());
                user.setDeletionState(deletionState);
                userRepository.save(user);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid deletion state: " + state);
            }
        }
        
        registrationRepository.findByUserId(userId).forEach(registration -> {
            Event event = registration.getEvent();
            if (event != null && event.getCurrentAttendees() > 0) {
                event.setCurrentAttendees(event.getCurrentAttendees() - 1);
                eventRepository.save(event);
            }
            registrationRepository.delete(registration);
        });
        sessionManager.invalidateSession(userId);
        userRepository.delete(user);
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

    public boolean hasAdminAccount() {
        return userRepository.existsByRole(UserRole.ADMIN);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + id));
    }

    private String generateRegistrationNo() {
        return "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}