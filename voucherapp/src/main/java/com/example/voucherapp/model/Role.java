package com.example.voucherapp.model;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity 
public class Role implements GrantedAuthority {
@Id 
@GeneratedValue (strategy = GenerationType.IDENTITY)
private long id;
private String name;

@ManyToMany (mappedBy = "roles")
private Set<User> Users;


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getAuthority() {
        
        return name;
    }


}
