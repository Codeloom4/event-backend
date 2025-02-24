package com.codeloon.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageDto {

    @NotBlank(message = "Package code is required")
    @Size(min = 4, max = 10, message = "Package code must be between 4 and 10 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Package code must be alphanumeric")
    private String id;

    @NotBlank(message = "Package name is required")
    private String name;

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotBlank(message = "Package description is required")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;
}
