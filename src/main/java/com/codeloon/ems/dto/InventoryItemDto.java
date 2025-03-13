package com.codeloon.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.message.StringFormattedMessage;

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
    private Integer minOrderQty;
    private String category;
    private String description;
}
