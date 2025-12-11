package com.flashcards.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Users")
public class User extends BaseEntity {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16 characters long")
    @Column(name = "Username", unique = true)
    private String username;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(name = "Email", unique = true)
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

    public Collection<? extends GrantedAuthority> getAuthorities() { ///  Na razie puste, przy obsludze rol cos z tym zrobie
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    ///  To samo z metodami ponizej, na razie do dzialania jwt wszystko domyslnie na true
    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
