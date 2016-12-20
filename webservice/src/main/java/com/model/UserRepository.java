package com.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <h1>JPA Repository</h1> 
 * This class serves as a repository for the user entity.
 * Defines all read and write operations to the database.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
@Component
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}