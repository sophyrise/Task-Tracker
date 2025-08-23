package com.sophie.task_tracker.mappers;

import com.sophie.task_tracker.dto.TaskDto;
import com.sophie.task_tracker.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ProjectMapper.class})
public interface TaskMapper {
    
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "assignedUserId", source = "assignedUser.id")
    @Mapping(target = "assignedUserEmail", source = "assignedUser.email")
    TaskDto toDto(Task task);   // Task - > taskDto
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    Task toEntity(TaskDto taskDto); // taskDto -> Task
}
