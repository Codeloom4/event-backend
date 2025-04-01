package com.codeloon.ems.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemUserStatusDto {
    private String userId;
    private String username;
    private String email;
    private String position;
    private String mobile;
    private String address;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private boolean forcePasswordChange;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoggedIn;
    private String roles;
}

