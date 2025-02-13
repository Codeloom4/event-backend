package com.codeloon.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryItemDto {

    private Long id;
    private String itemName;
    private Boolean isRefundable;
    private Long avgPrice;
    private Integer quantity;
    private LocalDateTime updatedAt;
    private String createdUser;
}
