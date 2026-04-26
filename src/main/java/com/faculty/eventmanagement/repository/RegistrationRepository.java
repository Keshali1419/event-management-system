package com.faculty.eventmanagement.repository;

import com.faculty.eventmanagement.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByUserId(Long userId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}