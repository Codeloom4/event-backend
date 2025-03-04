package com.codeloon.ems.dto;

import com.codeloon.ems.model.PackageTypeBean;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackageInfoDTO {
    private String id;
    private String name;
    private String description;
    private String eventType;
    private String eventDescription;
    private String packageType;
    private String packageTypeDescription;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;
}
