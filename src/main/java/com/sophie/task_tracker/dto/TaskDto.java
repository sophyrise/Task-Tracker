package com.sophie.task_tracker.dto;

import com.sophie.task_tracker.enums.TaskPriority;
import com.sophie.task_tracker.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private TaskPriority priority;
    private Long projectId;
    private String projectName;
    private Long assignedUserId;
    private String assignedUserEmail;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
