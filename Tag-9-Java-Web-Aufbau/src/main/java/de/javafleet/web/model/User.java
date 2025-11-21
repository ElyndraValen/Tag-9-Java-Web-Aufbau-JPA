package de.javafleet.web.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * User Model - Plain Old Java Object (POJO)
 * Repräsentiert einen Benutzer in der Datenbank
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String email;
    private String password;
    
    // Konstruktoren
    public User() {
    }
    
    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // Getters und Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // equals und hashCode für korrekte Vergleiche
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    // toString für Debugging
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
