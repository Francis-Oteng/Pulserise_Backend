package com.pulserise.pulserise.dto;

public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private boolean emailVerified;

    public User() {}

    public User(Long id, String email, String password, String name, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.emailVerified = emailVerified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
} 