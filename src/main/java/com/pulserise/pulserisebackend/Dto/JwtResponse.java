package com.pulserise.pulserisebackend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class JwtResponse {
    @JsonProperty("accessToken")
    private String accessToken;
    
    @JsonProperty("tokenType")
    private String tokenType = "Bearer";
    
    @JsonProperty("userId")
    private Long id;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("profileCompleted")
    private boolean profileCompleted;
    
    @JsonProperty("roles")
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, boolean profileCompleted, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileCompleted = profileCompleted;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}