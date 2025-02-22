package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserStatusBean {
    private String userId;
    private String username;
    private String email;
    private String position;
    private String mobile;
    private String address;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean accountNonLocked;
    private Boolean forcePasswordChange;
    private Timestamp createdAt;
}

