package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsBean {

    private String orderId;
    private String packageId;
    private String cusNote;
    private BigDecimal total_amount;
    private LocalDate eventDate;
    private LocalDateTime requestedDate;
    private String cusId;
    private String orderStatus;
    private String orderStatusDes;
    private String paymentStatus;
    private String adminRemark;
    private String approvedUser;
    private List<OrderDetailListBean> orderDetailListBeanList;




}
