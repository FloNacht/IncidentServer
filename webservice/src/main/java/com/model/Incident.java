package com.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Incident {
	
	@JsonIgnore
    @ManyToOne
    private User user;

    @Id
    @GeneratedValue
    private Long id;
    private String location;
    private String exactLocation;
    private String description;
    private String imagePath;
    
    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean active;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @CreationTimestamp
    private Timestamp timestamp;

    Incident() { // jpa only
    }

    public Incident(User user, String location, String exactLocation, String description, String imagePath) {
        this.user = user;
    	this.location = location;
    	this.exactLocation = exactLocation;
        this.description = description;
        this.imagePath = imagePath;
        this.active = true;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }
    
    public Timestamp getTimestamp() {
    	return timestamp;
    }

    public String getLocation() {
        return location;
    }

	public String getExactLocation() {
		return exactLocation;
	}
	
    public String getDescription() {
        return description;
    }

	public String getImagePath() {
		return imagePath;
	}
	
	public boolean getActice() {
		return active;
	}
	
	public void setActive(boolean b) {
		active = b;
	}
}