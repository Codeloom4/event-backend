package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockReportBean {
    private String itemName;
    private Integer availableQuantity;
    private Integer orderedQuantity;
    private Integer soldQuantity;
    private Boolean isRefundable;
}

