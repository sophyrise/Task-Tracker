package com.sophie.task_tracker.dto;

import com.sophie.task_tracker.enums.TaskPriority;
import com.sophie.task_tracker.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskUpdateDto {
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private TaskPriority priority;
    private Long assignedUserId;
}
