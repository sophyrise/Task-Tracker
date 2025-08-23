package com.sophie.task_tracker.repositories;

import com.sophie.task_tracker.entities.Project;
import com.sophie.task_tracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId")
    List<Project> findByOwnerId(@Param("ownerId") Long ownerId);
    // find project with owner_id
    
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId")
    Page<Project> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
    // finds owner_id's projects
    
    boolean existsByNameAndOwner(String name, User owner);
    // returns whether or not such project exists with this project name and owner
}
