package com.codeloon.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportCostDto {
    private Long id;
    private Long districtId;
    private Double deliveryFee;
}

