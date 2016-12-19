package com.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IncidentRepository extends JpaRepository<Incident, Long> {
	List<Incident> findByUserUsername(String username);
    List<Incident> findByActiveTrue();
    List<Incident> findByActiveFalse();
    
}
