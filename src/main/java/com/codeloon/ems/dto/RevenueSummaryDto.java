package com.codeloon.ems.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenueSummaryDto {
    LocalDate getDate();
    BigDecimal getRevenue();
}

