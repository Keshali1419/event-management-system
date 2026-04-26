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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final SessionManager sessionManager;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-z]+@faculty\\.edu$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*$");
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");
    private static final Pattern STUDENT_REG_PATTERN = Pattern.compile("^STU\\d{3}$");
    private static final Pattern LECTURER_REG_PATTERN = Pattern.compile("^LEC\\d{3}$");
    private static final Pattern ADMIN_REG_PATTERN = Pattern.compile("^ADM\\d{3}$");

    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User details are required.");
        }

        String fullName = user.getFullName() == null ? "" : user.getFullName().trim().replaceAll("\\s+", " ");
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        String phone = user.getPhone() == null ? "" : user.getPhone().trim();
        String password = user.getPassword() == null ? "" : user.getPassword();
        UserRole role = user.getRole() == null ? UserRole.STUDENT : user.getRole();
        String registrationNo = user.getRegistrationNo() == null
                ? ""
                : user.getRegistrationNo().trim().toUpperCase();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || registrationNo.isEmpty()) {
            throw new IllegalArgumentException("Registration no, full name, phone, email and password are required.");
        }

        if (!NAME_PATTERN.matcher(fullName).matches()) {
            throw new IllegalArgumentException("Full name must start each word with a capital letter (e.g., Kasun Perera).");
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email must follow this format: firstname@faculty.edu");
        }

        if (!STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters and include uppercase, lowercase, number and special character.");
        }

        if (!isRegistrationNoValidForRole(registrationNo, role)) {
            throw new IllegalArgumentException(registrationRuleMessage(role));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account already exists for this email.");
        }

        if (userRepository.findByRegistrationNo(registrationNo).isPresent()) {
            throw new IllegalArgumentException("This registration number is already in use.");
        }

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setRegistrationNo(registrationNo);
        user.setPassword(passwordEncoder.encode(password));

        User saved = userRepository.save(user);


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

        if (!NAME_PATTERN.matcher(fullName).matches()) {
            throw new IllegalArgumentException("Full name must start each word with a capital letter (e.g., Kasun Perera).");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email must follow this format: firstname@faculty.edu");
        }

        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
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
            if (!STRONG_PASSWORD_PATTERN.matcher(updatedUser.getPassword()).matches()) {
                throw new IllegalArgumentException("Password must be at least 8 characters and include uppercase, lowercase, number and special character.");
            }
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }


        UserSession session = new UserSession(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
        sessionManager.saveSession(session);

        return user;
    }


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

    private boolean isRegistrationNoValidForRole(String registrationNo, UserRole role) {
        return switch (role) {
            case STUDENT -> STUDENT_REG_PATTERN.matcher(registrationNo).matches();
            case LECTURER -> LECTURER_REG_PATTERN.matcher(registrationNo).matches();
            case ADMIN -> ADMIN_REG_PATTERN.matcher(registrationNo).matches();
        };
    }

    private String registrationRuleMessage(UserRole role) {
        return switch (role) {
            case STUDENT -> "Registration no for student must be like STU001.";
            case LECTURER -> "Registration no for lecturer must be like LEC001.";
            case ADMIN -> "Registration no for admin must be like ADM001.";
        };
    }
}