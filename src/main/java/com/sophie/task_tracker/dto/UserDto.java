package com.sophie.task_tracker.dto;

import com.sophie.task_tracker.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String email;
    private Role role;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
