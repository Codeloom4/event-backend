package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemListBean {

    private Long packageItemId;
    private String packageId;
    private Long inventoryItemId;
    private String itemName;
    private Integer quantity;
    private Double sellPrice;
    private Double bulkPrice;
}
