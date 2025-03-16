package com.codeloon.ems.dto;

import com.codeloon.ems.entity.Event;
import com.codeloon.ems.entity.PackageType;
import com.codeloon.ems.entity.User;
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

    @NotBlank(message = "Package type is required")
    private String type;

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotBlank(message = "Package description is required")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;

    public PackageDto(String id, String name, PackageType packageType, Event event, String description, LocalDateTime createdAt, LocalDateTime updatedAt, User createdUser) {
    }
}
