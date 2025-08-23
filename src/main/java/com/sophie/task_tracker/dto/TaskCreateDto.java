package com.sophie.task_tracker.dto;

import com.sophie.task_tracker.enums.TaskPriority;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class TaskCreateDto {
    @NotBlank(message = "Task title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    private LocalDate dueDate;
    
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    private Long assignedUserId;
}
