package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.model.LowStockReportBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LowStockReportRepository extends JpaRepository<Inventory, Long> {

    @Query(nativeQuery = true, value =
            "SELECT i.item_name as itemName, " +
                    "i.balance_qty as availableQuantity, " +
                    "i.order_qty as orderedQuantity " +
                    "FROM inventory i " +
                    "WHERE i.balance_qty < :threshold " +
                    "ORDER BY i.balance_qty ASC")
    List<LowStockReportBean> getLowStockReport(@Param("threshold") int threshold);
}

