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
@Table(name = "Inventory")

public class Inventory {
//    @Id
//    @Column(name = "id", updatable = false, nullable = false, length = 50)
//    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false)
    private String itemName;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "item_Id", referencedColumnName = "id", nullable = false)
//    private InventoryItem itemId;

    @Column(name = "item_Id",nullable = false)
    private Long itemId;

    @Column(name = "is_refundable", nullable = false)
    private Boolean isRefundable;

    @Column(name = "purchase_price",nullable = false)
    private Long purchasePrice;

    @Column(name = "sales_price",nullable = false)
    private Long salesPrice;

    @Column(name = "order_qty", nullable = false)
    private Integer orderQuantity;

    @Column(name = "sales_qty", nullable = false)
    private Integer salesQuantity;

    @Column(name = "balance_qty", nullable = false)
    private Integer balanceQuantity;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "start_Barcode")
    private String startBarcode;

    @Column(name = "end_Barcode")
    private String endBarcode;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "created_user", referencedColumnName = "username", nullable = false)
    private User createdUser;

//    @Column(name = "created_user")
//    private String createdUser;
}
