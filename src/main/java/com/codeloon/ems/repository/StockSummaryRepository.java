package com.codeloon.ems.repository;

import com.codeloon.ems.dto.StockSummaryDto;
import com.codeloon.ems.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockSummaryRepository extends JpaRepository<Inventory, Long> {

    @Query(nativeQuery = true, value =
            "SELECT i.item_name as itemName, i.balance_qty as quantity " +
                    "FROM inventory i " +
                    "ORDER BY i.balance_qty DESC " +
                    "LIMIT 10")
    List<StockSummaryDto> getStockSummary();
}

