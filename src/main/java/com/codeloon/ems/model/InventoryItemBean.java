package com.codeloon.ems.model;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryItemBean {
    private Long id;
    private String itemName;
    private String category;
}
