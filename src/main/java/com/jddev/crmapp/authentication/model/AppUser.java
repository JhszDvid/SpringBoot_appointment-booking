package com.jddev.crmapp.authentication.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, unique = false)
    private String password;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    protected AppUser(){};

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public static class Builder {
        private AppUser newUser;
        public Builder()
        {
            newUser = new AppUser();
        }

        public Builder withUsername(String username)
        {
            newUser.setEmail(username);
            return this;
        }

        public Builder withPassword(String pw)
        {
            newUser.setPassword(pw);
            return this;
        }

        public Builder withLastLogin(LocalDateTime datetime)
        {
            newUser.setLastLogin(datetime);
            return this;
        }

        public Builder withVerified(boolean isVerified)
        {
            newUser.setIsVerified(isVerified);
            return this;
        }

        public AppUser build(){
            return newUser;
        }
    }
}