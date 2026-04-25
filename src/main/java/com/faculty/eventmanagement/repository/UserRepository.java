package com.faculty.eventmanagement.repository;

import com.faculty.eventmanagement.model.User;
import com.faculty.eventmanagement.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRegistrationNo(String registrationNo);
    boolean existsByRole(UserRole role);
}