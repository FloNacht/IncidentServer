package com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

/**
 * <h1>JPA Entity</h1> 
 * This JPA class defines all information of the "user" table in the database.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
@Entity
public class User {

    @OneToMany(mappedBy = "user")
    private Set<Incident> incidents = new HashSet<>();

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, length = 64, columnDefinition = "varchar(64)", nullable = false)
    private String username;
    
    @Column(length = 32, columnDefinition = "varchar(32)", nullable = false)
    private String password;
    
    @Column(length = 32, columnDefinition = "varchar(32) default 'STUDENT'", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    public User() { // jpa only
    }
    
    public User(String name, String password) {
        this.username = name;
        this.password = password;
        this.role = Role.STUDENT;
    }
    
    public User(String name, String password, Role role) {
        this.username = name;
        this.password = password;
        this.role = role;
    }
    
    public Set<Incident> getIncidents() {
        return incidents;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

	public Role getRole() {
		return role;
	}
}