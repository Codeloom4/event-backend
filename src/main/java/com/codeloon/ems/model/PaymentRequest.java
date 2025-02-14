package com.codeloon.ems.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
}

