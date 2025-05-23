package com.codeloon.ems.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "inventoryItem")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "is_refundable")
    private Boolean isRefundable;

    @Column(name = "average_price")
    private Double avgPrice;

    @Column(name = "order_qty")
    private Integer quantity;

    @Column(name = "updated_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "created_user", nullable = false)
    private String createdUser;

    @Column(name = "min_order_qty")
    private Integer minOrderQty;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description")
    private String description;
}
