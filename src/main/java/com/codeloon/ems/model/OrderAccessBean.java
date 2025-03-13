package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAccessBean {

    private String packageId;
    private Double total_price;
    private String packageTypeCode;
    private String packageTypeDes;
    private List<OrderItemListBean> orderItemListBeanList;

}
