package com.pulserise.pulserise_backend.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name="user")

public class User implements UserDetails{

    @Id
    @GeneratedValue
private Integer id;

private String firstname;

private String lastname;

private String  email;

private String password;

private String verificationToken;

    public User() {

    }

    public String getVerificationToken() {
    return verificationToken;
}

public void setVerificationToken(String verificationToken) {
    this.verificationToken = verificationToken;
}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    public String  getpassword(){
            return password;

    }
}