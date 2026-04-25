package com.faculty.eventmanagement.services;

import com.faculty.eventmanagement.model.UserRoleType;
import com.faculty.eventmanagement.repository.UserRoleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleTypeService {

    private final UserRoleTypeRepository repository;

    public UserRoleType addUserRoleType(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Role name is required");
        }
        if (repository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Role type '" + name + "' already exists");
        }
        UserRoleType roleType = new UserRoleType();
        roleType.setName(name);
        return repository.save(roleType);
    }

    public List<UserRoleType> getAllUserRoleTypes() {
        return repository.findAllByOrderByNameAsc();
    }

    public UserRoleType getUserRoleTypeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User role type not found"));
    }

    public UserRoleType updateUserRoleType(Long id, String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Role name is required");
        }

        UserRoleType existing = getUserRoleTypeById(id);
        repository.findByNameIgnoreCase(name)
                .filter(type -> !type.getId().equals(id))
                .ifPresent(type -> {
                    throw new IllegalArgumentException("Role type '" + name + "' already exists");
                });

        existing.setName(name);
        return repository.save(existing);
    }

    public void deleteUserRoleType(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("User role type not found");
        }
        repository.deleteById(id);
    }
}
