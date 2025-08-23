package com.sophie.task_tracker.repositories;

import com.sophie.task_tracker.entities.User;
import com.sophie.task_tracker.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);   // avoids null
    // find user by email
    
    boolean existsByEmail(String email);
    // returns whether or not such user with that email exists
    
    List<User> findByRole(Role role);
    // List all users with such role
}
