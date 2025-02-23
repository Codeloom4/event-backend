package com.codeloon.ems.repository;

import com.codeloon.ems.dto.RevenueSummaryDto;
import com.codeloon.ems.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevenueSummaryRepository extends JpaRepository<Inventory, Long> {

    @Query(nativeQuery = true, value =
            "SELECT DATE(i.updated_at) as date,  CAST(SUM(i.sales_qty * i.sales_price) AS DECIMAL(19, 2)) AS revenue " +
                    "FROM inventory i " +
                    "WHERE i.updated_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                    "GROUP BY DATE(i.updated_at) " +
                    "ORDER BY DATE(i.updated_at)")
    List<RevenueSummaryDto> getRevenueSummary();
}

