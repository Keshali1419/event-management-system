package com.faculty.eventmanagement.controller;

import com.faculty.eventmanagement.model.UserRoleType;
import com.faculty.eventmanagement.services.UserRoleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-role-types")
@RequiredArgsConstructor
public class UserRoleTypeController {

    private final UserRoleTypeService service;

    @PostMapping
    public ResponseEntity<?> addUserRoleType(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Role name is required");
            }
            UserRoleType roleType = service.addUserRoleType(name);
            return ResponseEntity.ok(roleType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserRoleType>> getAllUserRoleTypes() {
        return ResponseEntity.ok(service.getAllUserRoleTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleType> getUserRoleTypeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getUserRoleTypeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserRoleType(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            UserRoleType updated = service.updateUserRoleType(id, request.get("name"));
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserRoleType(@PathVariable Long id) {
        try {
            service.deleteUserRoleType(id);
            return ResponseEntity.ok("User role type deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
