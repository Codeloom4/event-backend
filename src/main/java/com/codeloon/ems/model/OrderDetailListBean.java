package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailListBean {
    private Integer orderDetailId;
    private String orderId;
    private Long inventoryItemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal bulkPrice;
    private String itemCategory;
    private Integer itemBalance;
}
