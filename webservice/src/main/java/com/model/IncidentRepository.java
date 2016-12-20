package com.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1>JPA Repository</h1> 
 * This class serves as a repository for the incident entity.
 * Defines all read and write operations to the database.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
@Component
public interface IncidentRepository extends JpaRepository<Incident, Long> {
	List<Incident> findByUserUsername(String username);
    List<Incident> findByActiveTrue();
    List<Incident> findByActiveFalse();
    
}
