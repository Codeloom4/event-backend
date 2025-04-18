package com.codeloon.ems.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_request")
public class OrderRequest {
    @Id
    @Column(name = "order_id", length = 50)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "package_id", referencedColumnName = "id", nullable = false)
    private Package packageId;

    @Column(name = "cus_note", columnDefinition = "TEXT")
    private String customerNote;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "requested_date", nullable = false, updatable = false)
    private LocalDateTime requestedDate;

//    @Column(name = "cus_username", length = 20)
//    private String customerUsername;

    @ManyToOne
    @JoinColumn(name = "cus_username", referencedColumnName = "username")
    private User customerUsername;

    @Column(name = "lastupdated_datetime")
    private LocalDateTime lastUpdatedDatetime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "order_status", referencedColumnName = "code")
    private Status orderStatus;

    @ManyToOne
    @JoinColumn(name = "payment_status", referencedColumnName = "code")
    private Status paymentStatus;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "approved_user", length = 20)
    private String approvedUser;

    @Column(name = "ref_status", length = 50)
    private String refStatus;

    @Column(name = "delivery_fee")
    private Double deliveryFee;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "address")
    private String address;
}
