package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestBean {

    private String packageId;
    private Double total_price;
    private String packageTypeCode;
    private String packageTypeDes;
    private List<OrderItemListBean> orderItemListBeanList;

    private String cusNotes;
    private Date eventDate;

}
