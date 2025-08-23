package com.sophie.task_tracker.mappers;

import com.sophie.task_tracker.dto.UserDto;
import com.sophie.task_tracker.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDto toDto(User user);   // user -> userDto
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    User toEntity(UserDto userDto); // userDto -> user
    // when i run the app, mapstruct will generate implementations of these methods in target folder
}
