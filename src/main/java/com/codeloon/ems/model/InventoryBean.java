package com.codeloon.ems.model;

import com.codeloon.ems.entity.User;

import java.time.LocalDateTime;

public class InventoryBean {
    private Long id;
    private String itemName;
    private Boolean isRefundable;
    private Long purchasePrice;
    private Long salesPrice;
    private Integer orderQuantity;
    private Integer salesQuantity;
    private Integer balanceQuantity;
    private String barcode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User CreatedUser;
}
