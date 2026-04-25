package com.faculty.eventmanagement.repository;

import com.faculty.eventmanagement.model.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleTypeRepository extends JpaRepository<UserRoleType, Long> {
    Optional<UserRoleType> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<UserRoleType> findAllByOrderByNameAsc();
}
