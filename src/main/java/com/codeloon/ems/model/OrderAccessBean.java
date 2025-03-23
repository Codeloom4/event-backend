package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAccessBean {

    private String packageId;
    private Double total_price;
    private String packageTypeCode;
    private String packageTypeDes;
    private String eventType;
    private List<OrderItemListBean> orderItemListBeanList;

    private String cusNote;
    private LocalDate eventDate;

}
