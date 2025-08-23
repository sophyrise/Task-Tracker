package com.sophie.task_tracker.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ProjectCreateDto {
    @NotBlank(message = "Project name is required")
    private String name;
    
    private String description;
}
