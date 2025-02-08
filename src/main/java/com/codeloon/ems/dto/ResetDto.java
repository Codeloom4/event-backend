package com.codeloon.ems.dto;

import lombok.Data;

@Data
public class ResetDto {
    private String userName;
    private String oldPassword;
    private String password;
}
