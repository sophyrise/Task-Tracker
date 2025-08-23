package com.sophie.task_tracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerEmail;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
