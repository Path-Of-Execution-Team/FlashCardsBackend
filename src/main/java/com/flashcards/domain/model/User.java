package com.flashcards.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Users")
public class User extends BaseEntity {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16 characters long")
    @Column(name = "Username")
    private String username;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(name = "Email")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    @Column(name = "PasswordHash")
    private String passwordHash;

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
