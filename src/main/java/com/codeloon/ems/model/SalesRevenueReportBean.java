package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesRevenueReportBean {
    private String customerName;
    private String orderId;
    private String packageId;
    private BigDecimal averagePrice;
    private Integer quantity;
    private String itemName;
    private String category;
    private BigDecimal totalRevenue;
}