package com.sophie.task_tracker.services;

import com.sophie.task_tracker.dto.TaskCreateDto;
import com.sophie.task_tracker.dto.TaskDto;
import com.sophie.task_tracker.dto.TaskUpdateDto;
import com.sophie.task_tracker.entities.Project;
import com.sophie.task_tracker.entities.Task;
import com.sophie.task_tracker.entities.User;
import com.sophie.task_tracker.enums.Role;
import com.sophie.task_tracker.enums.TaskPriority;
import com.sophie.task_tracker.enums.TaskStatus;
import com.sophie.task_tracker.mappers.TaskMapper;
import com.sophie.task_tracker.repositories.ProjectRepository;
import com.sophie.task_tracker.repositories.TaskRepository;
import com.sophie.task_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskDto createTask(TaskCreateDto taskCreateDto, Long userId, Role userRole) {
        Project project = projectRepository.findById(taskCreateDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + taskCreateDto.getProjectId()));

        // Check if user has access to the project
        if (!hasAccessToProject(project, userId, userRole)) {
            throw new RuntimeException("Access denied to project");
        }

        Task task = new Task();
        task.setTitle(taskCreateDto.getTitle());
        task.setDescription(taskCreateDto.getDescription());
        task.setProject(project);
        task.setDueDate(taskCreateDto.getDueDate());
        task.setPriority(taskCreateDto.getPriority());
        task.setStatus(TaskStatus.TODO);

        // Assign user if specified (MANAGER/ADMIN only)
        if (taskCreateDto.getAssignedUserId() != null) {
            if (userRole != Role.MANAGER && userRole != Role.ADMIN) {
                throw new RuntimeException("Only MANAGER/ADMIN can assign users to tasks");
            }
            User assignedUser = userRepository.findById(taskCreateDto.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found with id: " + taskCreateDto.getAssignedUserId()));
            task.setAssignedUser(assignedUser);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    public TaskDto getTaskById(Long taskId, Long userId, Role userRole) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Check access permissions
        if (!hasAccessToTask(task, userId, userRole)) {
            throw new RuntimeException("Access denied to task");
        }

        return taskMapper.toDto(task);
    }

    public Page<TaskDto> getTasksByProject(Long projectId, Long userId, Role userRole, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Check access permissions
        if (!hasAccessToProject(project, userId, userRole)) {
            throw new RuntimeException("Access denied to project");
        }

        Page<Task> tasks = taskRepository.findByProject(project, pageable);
        return tasks.map(taskMapper::toDto);
    }

    public Page<TaskDto> getTasksByAssignedUser(Long assignedUserId, Long userId, Role userRole, Pageable pageable) {
        // Users can only see their own assigned tasks, unless they're ADMIN
        if (userRole != Role.ADMIN && !assignedUserId.equals(userId)) {
            throw new RuntimeException("Access denied to view other user's tasks");
        }

        User assignedUser = userRepository.findById(assignedUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + assignedUserId));

        Page<Task> tasks = taskRepository.findByAssignedUser(assignedUser, pageable);
        return tasks.map(taskMapper::toDto);
    }

    public Page<TaskDto> getTasksByStatus(TaskStatus status, Long userId, Role userRole, Pageable pageable) {
        Page<Task> tasks;
        
        if (userRole == Role.ADMIN) {
            // Admin can see all tasks
            tasks = taskRepository.findByStatus(status, pageable);
        } else {
            // Others can only see their own tasks
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            tasks = taskRepository.findByStatusAndAssignedUser(status, user, pageable);
        }
        
        return tasks.map(taskMapper::toDto);
    }

    public Page<TaskDto> getTasksByPriority(TaskPriority priority, Long userId, Role userRole, Pageable pageable) {
        Page<Task> tasks;
        
        if (userRole == Role.ADMIN) {
            // Admin can see all tasks
            tasks = taskRepository.findByPriority(priority, pageable);
        } else {
            // Others can only see their own tasks
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            tasks = taskRepository.findByPriorityAndAssignedUser(priority, user, pageable);
        }
        
        return tasks.map(taskMapper::toDto);
    }

    public TaskDto updateTask(Long taskId, TaskUpdateDto taskUpdateDto, Long userId, Role userRole) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Check access permissions
        if (!hasAccessToTask(task, userId, userRole)) {
            throw new RuntimeException("Access denied to task");
        }

        // Only assigned user can update status
        if (taskUpdateDto.getStatus() != null && 
            (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(userId))) {
            throw new RuntimeException("Only assigned user can update task status");
        }

        // Only MANAGER/ADMIN can assign users
        if (taskUpdateDto.getAssignedUserId() != null && 
            userRole != Role.MANAGER && userRole != Role.ADMIN) {
            throw new RuntimeException("Only MANAGER/ADMIN can assign users to tasks");
        }

        // Update fields
        if (taskUpdateDto.getTitle() != null) {
            task.setTitle(taskUpdateDto.getTitle());
        }
        if (taskUpdateDto.getDescription() != null) {
            task.setDescription(taskUpdateDto.getDescription());
        }
        if (taskUpdateDto.getStatus() != null) {
            task.setStatus(taskUpdateDto.getStatus());
        }
        if (taskUpdateDto.getDueDate() != null) {
            task.setDueDate(taskUpdateDto.getDueDate());
        }
        if (taskUpdateDto.getPriority() != null) {
            task.setPriority(taskUpdateDto.getPriority());
        }
        if (taskUpdateDto.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(taskUpdateDto.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found with id: " + taskUpdateDto.getAssignedUserId()));
            task.setAssignedUser(assignedUser);
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    public void deleteTask(Long taskId, Long userId, Role userRole) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Check access permissions
        if (!hasAccessToTask(task, userId, userRole)) {
            throw new RuntimeException("Access denied to task");
        }

        taskRepository.delete(task);
    }

    public Page<TaskDto> getTasksDueBefore(LocalDate date, Long userId, Role userRole, Pageable pageable) {
        List<Task> tasks;
        
        if (userRole == Role.ADMIN) {
            // Admin can see all tasks
            tasks = taskRepository.findByDueDateBefore(date);
        } else {
            // Others can only see their own tasks
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            tasks = taskRepository.findByDueDateBeforeAndAssignedUser(date, user);
        }
        
        List<TaskDto> list = tasks.stream()
                .map(taskMapper::toDto)
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        List<TaskDto> content = start > end ? List.of() : list.subList(start, end);
        return new PageImpl<>(content, pageable, list.size());
    }

    private boolean hasAccessToTask(Task task, Long userId, Role userRole) {
        // Admin has access to all tasks
        if (userRole == Role.ADMIN) {
            return true;
        }
        
        // Project owner has access
        if (task.getProject().getOwner().getId().equals(userId)) {
            return true;
        }
        
        // Assigned user has access
        return task.getAssignedUser() != null && task.getAssignedUser().getId().equals(userId);
    }

    private boolean hasAccessToProject(Project project, Long userId, Role userRole) {
        // Admin has access to all projects
        if (userRole == Role.ADMIN) {
            return true;
        }
        
        // Project owner has access
        return project.getOwner().getId().equals(userId);
    }
}
