package com.codeloon.ems.dto;

import com.codeloon.ems.entity.Package;
import com.codeloon.ems.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDto {

    private String orderId;
    private Package packageId;
    private String customerNote;
    private BigDecimal total;
    private LocalDate eventDate;
    private LocalDateTime requestedDate;
    private String customerUsername;
    private LocalDateTime lastUpdatedDatetime;
    private Status orderStatus;
    private Status paymentStatus;
    private String remark;
    private String approvedUser;
}
