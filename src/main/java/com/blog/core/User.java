package com.blog.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

import java.security.Principal;

@Entity
@Table(name = "User")

@NamedQueries({
        @NamedQuery(
                name = "com.blog.core.User.findByUsername",
                query = "SELECT u FROM User u WHERE u.username = :username"
        ),
        @NamedQuery(
                name = "com.blog.core.User.findAll",
                query = "SELECT u FROM User u"
        )
})
public class User implements Principal {

    @Id
    @NotBlank(message = "username cannot be empty")
    @JsonProperty("name")
    private String username;

    private String role;
    @NotBlank(message = "password cannot be empty")
    @JsonProperty("password")
    private String password;

    public User() {
    }

    public User(String username, String role, String password) {
        this.username = username;
        this.role = role;
        this.password = password;
    }

    public String getName() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
    public void setRole(String role){
        this.role=role;
    }
    public void setPassword(String password){
        this.password=password;
    }
    @JsonIgnore
    public boolean isAdmin(){
       return role.equals("admin");
    }
}
