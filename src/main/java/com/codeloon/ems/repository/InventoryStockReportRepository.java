package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.model.InventoryStockReportBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryStockReportRepository extends JpaRepository<Inventory, Long> {

    @Query(nativeQuery = true, value =
            "SELECT i.item_name as itemName, " +
                    "i.balance_qty as availableQuantity, " +
                    "i.order_qty as orderedQuantity, " +
                    "i.sales_qty as soldQuantity, " +
                    "i.is_refundable as isRefundable " +
                    "FROM inventory i " +
                    "JOIN inventory_item ii ON i.item_id = ii.id " +
                    "ORDER BY i.item_name")
    List<InventoryStockReportBean> getInventoryStockReport();
}

