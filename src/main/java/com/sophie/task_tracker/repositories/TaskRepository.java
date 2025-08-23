package com.sophie.task_tracker.repositories;

import com.sophie.task_tracker.entities.Project;
import com.sophie.task_tracker.entities.Task;
import com.sophie.task_tracker.entities.User;
import com.sophie.task_tracker.enums.TaskPriority;
import com.sophie.task_tracker.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by project
    List<Task> findByProject(Project project);
    Page<Task> findByProject(Project project, Pageable pageable);
    
    // Find tasks by assigned user
    List<Task> findByAssignedUser(User assignedUser);
    Page<Task> findByAssignedUser(User assignedUser, Pageable pageable);
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    
    // Find tasks by priority
    List<Task> findByPriority(TaskPriority priority);
    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);
    
    // Find tasks by status and assigned user
    Page<Task> findByStatusAndAssignedUser(TaskStatus status, User assignedUser, Pageable pageable);
    
    // Find tasks by priority and assigned user
    Page<Task> findByPriorityAndAssignedUser(TaskPriority priority, User assignedUser, Pageable pageable);

    // Find tasks by due date
    List<Task> findByDueDateBefore(LocalDate dueDate);
    
    // Find tasks by due date and assigned user
    List<Task> findByDueDateBeforeAndAssignedUser(LocalDate dueDate, User assignedUser);
    

}
