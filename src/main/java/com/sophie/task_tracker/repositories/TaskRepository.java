package com.sophie.task_tracker.repositories;

import com.sophie.task_tracker.entities.Project;
import com.sophie.task_tracker.entities.Task;
import com.sophie.task_tracker.entities.User;
import com.sophie.task_tracker.enums.TaskPriority;
import com.sophie.task_tracker.enums.TaskStatus;
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
    
    // Find tasks by assigned user
    List<Task> findByAssignedUser(User assignedUser);
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    
    // Find tasks by priority
    List<Task> findByPriority(TaskPriority priority);
    
    // Find tasks by status and assigned user
    List<Task> findByStatusAndAssignedUser(TaskStatus status, User assignedUser);
    
    // Find tasks by priority and assigned user
    List<Task> findByPriorityAndAssignedUser(TaskPriority priority, User assignedUser);

    // Find tasks by due date
    List<Task> findByDueDateBefore(LocalDate dueDate);
    
    // Find tasks by due date and assigned user
    List<Task> findByDueDateBeforeAndAssignedUser(LocalDate dueDate, User assignedUser);
    

}
