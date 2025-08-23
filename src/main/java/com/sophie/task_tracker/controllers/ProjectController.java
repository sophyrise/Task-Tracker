package com.sophie.task_tracker.controllers;

import com.sophie.task_tracker.dto.ProjectCreateDto;
import com.sophie.task_tracker.dto.ProjectDto;
import com.sophie.task_tracker.enums.Role;
import com.sophie.task_tracker.services.ProjectService;
import com.sophie.task_tracker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;


    @PostMapping
    @Operation(summary = "Create project", description = "Create a new project (MANAGER/ADMIN only)")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<ProjectDto> createProject(
            @Valid @RequestBody ProjectCreateDto projectCreateDto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        ProjectDto project = projectService.createProject(projectCreateDto, userId);
        return ResponseEntity.ok(project);
    }



    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieve all projects (ADMIN sees all, others see their own)")
    public ResponseEntity<List<ProjectDto>> getAllProjects(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        List<ProjectDto> projects = projectService.getAllProjects(userId, userRole);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieve a specific project by ID")
    public ResponseEntity<ProjectDto> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        ProjectDto project = projectService.getProjectById(id, userId, userRole);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/my-projects")
    @Operation(summary = "Get my projects", description = "Retrieve projects owned by the authenticated user")
    public ResponseEntity<Page<ProjectDto>> getMyProjects(
            Pageable pageable,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Page<ProjectDto> projects = projectService.getProjectsByOwner(userId, pageable);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Update an existing project")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateDto projectUpdateDto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        ProjectDto project = projectService.updateProject(id, projectUpdateDto, userId, userRole);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Delete a project by ID")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        projectService.deleteProject(id, userId, userRole);
        return ResponseEntity.noContent().build();
    }



    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Unauthenticated request");
        }
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private Role getRoleFromAuthentication(Authentication authentication) {
        // Extract role from authentication authorities
        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            try {
                return Role.valueOf(authority);
            } catch (IllegalArgumentException e) {
                return Role.USER; // Default role if authority doesn't match
            }
        }
        return Role.USER; // Default role
    }

}