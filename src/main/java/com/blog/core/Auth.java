package com.blog.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "auth")
public class Auth {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "token")
    private String token;

    // Default constructor (required by Hibernate)
    public Auth() {
    }

    public Auth(String username, String token) {
        this.username = username;
        this.token = token;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token =token;
    }
}
