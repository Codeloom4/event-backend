package com.codeloon.ems.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageItemDto {

    @NotBlank(message = "Package id is required")
    private String package_id;

    @NotBlank(message = "Item is required")
    private String itemName;

    @NotBlank(message = "Selling price is required")
    private Double sellPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;
}
