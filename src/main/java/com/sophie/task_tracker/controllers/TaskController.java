package com.sophie.task_tracker.controllers;

import com.sophie.task_tracker.dto.TaskCreateDto;
import com.sophie.task_tracker.dto.TaskDto;
import com.sophie.task_tracker.dto.TaskUpdateDto;
import com.sophie.task_tracker.enums.Role;
import com.sophie.task_tracker.enums.TaskPriority;
import com.sophie.task_tracker.enums.TaskStatus;
import com.sophie.task_tracker.services.TaskService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;


    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task (MANAGER/ADMIN only)")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<TaskDto> createTask(
            @Valid @RequestBody TaskCreateDto taskCreateDto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        TaskDto task = taskService.createTask(taskCreateDto, userId, userRole);
        return ResponseEntity.ok(task);
    }



    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by ID")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        TaskDto task = taskService.getTaskById(id, userId, userRole);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project", description = "Retrieve all tasks for a specific project")
    public ResponseEntity<Page<TaskDto>> getTasksByProject(
            @PathVariable Long projectId,
            Pageable pageable,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        Page<TaskDto> tasks = taskService.getTasksByProject(projectId, userId, userRole, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned/{userId}")
    @Operation(summary = "Get tasks by assigned user", description = "Retrieve all tasks assigned to a specific user")
    public ResponseEntity<Page<TaskDto>> getTasksByAssignedUser(
            @PathVariable Long userId,
            Pageable pageable,
            Authentication authentication) {
        
        Long currentUserId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        Page<TaskDto> tasks = taskService.getTasksByAssignedUser(userId, currentUserId, userRole, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve all tasks with a specific status")
    public ResponseEntity<Page<TaskDto>> getTasksByStatus(
            @PathVariable TaskStatus status,
            Pageable pageable,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        Page<TaskDto> tasks = taskService.getTasksByStatus(status, userId, userRole, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority", description = "Retrieve all tasks with a specific priority")
    public ResponseEntity<Page<TaskDto>> getTasksByPriority(
            @PathVariable TaskPriority priority,
            Pageable pageable,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        Page<TaskDto> tasks = taskService.getTasksByPriority(priority, userId, userRole, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-before/{date}")
    @Operation(summary = "Get tasks due before date", description = "Retrieve tasks due before a date (paged)")
    public ResponseEntity<Page<TaskDto>> getTasksDueBefore(
            @PathVariable LocalDate date,
            Pageable pageable,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        Page<TaskDto> tasks = taskService.getTasksDueBefore(date, userId, userRole, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update an existing task (owner or assigned user)")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDto taskUpdateDto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        TaskDto task = taskService.updateTask(id, taskUpdateDto, userId, userRole);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update only the status of a task (assigned user only)")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        
        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setStatus(status);
        
        TaskDto task = taskService.updateTask(id, updateDto, userId, userRole);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete a task by ID (project owner or ADMIN)")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Role userRole = getRoleFromAuthentication(authentication);
        taskService.deleteTask(id, userId, userRole);
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