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

        if (!hasAccessToTask(task, userId, userRole)) {
            throw new RuntimeException("Access denied to task");
        }

        return taskMapper.toDto(task);
    }

    public List<TaskDto> getTasksByProject(Long projectId, Long userId, Role userRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        if (!hasAccessToProject(project, userId, userRole)) {
            throw new RuntimeException("Access denied to project");
        }

        List<Task> tasks = taskRepository.findByProject(project);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    public List<TaskDto> getTasksByAssignedUser(Long assignedUserId, Long userId, Role userRole) {
        if (userRole != Role.ADMIN && !assignedUserId.equals(userId)) {
            throw new RuntimeException("Access denied to view other user's tasks");
        }

        User assignedUser = userRepository.findById(assignedUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + assignedUserId));

        List<Task> tasks = taskRepository.findByAssignedUser(assignedUser);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    public List<TaskDto> getTasksByStatus(TaskStatus status, Long userId, Role userRole) {
        List<Task> tasks;
        
        if (userRole == Role.ADMIN) {
            tasks = taskRepository.findByStatus(status);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            tasks = taskRepository.findByStatusAndAssignedUser(status, user);
        }
        
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    public List<TaskDto> getTasksByPriority(TaskPriority priority, Long userId, Role userRole) {
        List<Task> tasks;
        
        if (userRole == Role.ADMIN) {
            tasks = taskRepository.findByPriority(priority);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            tasks = taskRepository.findByPriorityAndAssignedUser(priority, user);
        }
        
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    public TaskDto updateTask(Long taskId, TaskUpdateDto taskUpdateDto, Long userId, Role userRole) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!hasAccessToTask(task, userId, userRole)) {
            throw new RuntimeException("Access denied to task");
        }

        if (taskUpdateDto.getStatus() != null &&
            (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(userId))) {
            throw new RuntimeException("Only assigned user can update task status");
        }

        if (taskUpdateDto.getAssignedUserId() != null &&
            userRole != Role.MANAGER && userRole != Role.ADMIN) {
            throw new RuntimeException("Only MANAGER/ADMIN can assign users to tasks");
        }

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

        if (!hasAccessToTask(task, userId, userRole)) {
            throw new RuntimeException("Access denied to task");
        }

        taskRepository.delete(task);
    }

    public List<TaskDto> getTasksDueBefore(LocalDate date, Long userId, Role userRole) {
        List<Task> tasks;
        
        if (userRole == Role.ADMIN) {
            tasks = taskRepository.findByDueDateBefore(date);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            tasks = taskRepository.findByDueDateBeforeAndAssignedUser(date, user);
        }
        
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    private boolean hasAccessToTask(Task task, Long userId, Role userRole) {
        if (userRole == Role.ADMIN) {
            return true;
        }
        
        if (task.getProject().getOwner().getId().equals(userId)) {
            return true;
        }
        return task.getAssignedUser() != null && task.getAssignedUser().getId().equals(userId);
    }

    private boolean hasAccessToProject(Project project, Long userId, Role userRole) {
        if (userRole == Role.ADMIN) {
            return true;
        }
        return project.getOwner().getId().equals(userId);
    }
}
