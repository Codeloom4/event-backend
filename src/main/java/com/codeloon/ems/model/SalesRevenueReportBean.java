package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesRevenueReportBean {
    private String itemName;
    private Integer soldQuantity;
    private BigDecimal salesPrice;
    private BigDecimal totalRevenue;
}

