package com.codeloon.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private String id;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    //@StrongPassword
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    private Boolean enabled = true;

    private String position;

    @NotBlank(message = "Address cannot be empty")
    @Pattern(
            regexp = "^[A-Za-z0-9\\-\\s,]+$",
            message = "Invalid address format. Use letters, numbers, '-', ',' and spaces only."
    )
    private String address;

    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{10}$", message = "Invalid phone number")
    private String mobileNo;

    @Pattern(regexp = "ADMIN|EMPLOYEE|CLIENT", message = "Role should be ADMIN, EMPLOYEE or CLIENT")
    private String role;

    private Set<String> roles; // User roles (e.g., ADMIN, EMPLOYEE, CLIENT)
}
