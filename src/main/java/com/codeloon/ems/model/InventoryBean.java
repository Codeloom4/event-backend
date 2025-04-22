package com.codeloon.ems.model;

import com.codeloon.ems.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryBean {
    private Long id;
    private Long itemId;
    private String itemName;
    private Boolean isRefundable;
    private Double purchasePrice;
    private Double salesPrice;
    private Integer orderQuantity;
    private Integer salesQuantity;
    private Integer balanceQuantity;
    private Long startBarcode;
    private Long endBarcode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;
    private List<Long> barcodeList;
}
