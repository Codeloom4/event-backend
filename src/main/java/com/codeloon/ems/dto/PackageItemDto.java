package com.codeloon.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private String itemCode;

    @NotNull(message = "Bulk price is required")
    @Positive(message = "Bulk price must be greater than zero")
    private Double bulkPrice;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;


    private String itemName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;
}
