package com.sophie.task_tracker.services;

import com.sophie.task_tracker.dto.ProjectCreateDto;
import com.sophie.task_tracker.dto.ProjectDto;
import com.sophie.task_tracker.entities.Project;
import com.sophie.task_tracker.entities.User;
import com.sophie.task_tracker.enums.Role;
import com.sophie.task_tracker.mappers.ProjectMapper;
import com.sophie.task_tracker.repositories.ProjectRepository;
import com.sophie.task_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectCreateDto projectCreateDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));

        // Check if project name already exists for this owner
        if (projectRepository.existsByNameAndOwner(projectCreateDto.getName(), owner)) {
            throw new RuntimeException("Project with name '" + projectCreateDto.getName() + "' already exists for this user");
        }

        Project project = new Project();
        project.setName(projectCreateDto.getName());
        project.setDescription(projectCreateDto.getDescription());
        project.setOwner(owner);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    public ProjectDto getProjectById(Long projectId, Long userId, Role userRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Check access permissions
        if (!hasAccessToProject(project, userId, userRole)) {
            throw new RuntimeException("Access denied to project");
        }

        return projectMapper.toDto(project);
    }

    public Page<ProjectDto> getProjectsByOwner(Long ownerId, Pageable pageable) {
        Page<Project> projects = projectRepository.findByOwnerId(ownerId, pageable);
        return projects.map(projectMapper::toDto);
    }

    public List<ProjectDto> getAllProjects(Long userId, Role userRole) {
        List<Project> projects;
        
        if (userRole == Role.ADMIN) {
            // Admin can see all projects
            projects = projectRepository.findAll();
        } else {
            // Others can only see their own projects
            projects = projectRepository.findByOwnerId(userId);
        }
        
        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto updateProject(Long projectId, ProjectCreateDto projectUpdateDto, Long userId, Role userRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Check access permissions
        if (!hasAccessToProject(project, userId, userRole)) {
            throw new RuntimeException("Access denied to project");
        }

        // Check if new name conflicts with existing project
        if (!project.getName().equals(projectUpdateDto.getName()) && 
            projectRepository.existsByNameAndOwner(projectUpdateDto.getName(), project.getOwner())) {
            throw new RuntimeException("Project with name '" + projectUpdateDto.getName() + "' already exists for this user");
        }

        project.setName(projectUpdateDto.getName());
        project.setDescription(projectUpdateDto.getDescription());

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject);
    }

    public void deleteProject(Long projectId, Long userId, Role userRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Check access permissions
        if (!hasAccessToProject(project, userId, userRole)) {
            throw new RuntimeException("Access denied to project");
        }

        projectRepository.delete(project);
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
