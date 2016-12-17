package com.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    Collection<Incident> findByUserUsername(String username);
    Collection<Incident> findByActiveTrue();
    Collection<Incident> findByActiveFalse();
    
}
