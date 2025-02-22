package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.model.SalesRevenueReportBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRevenueReportRepository extends JpaRepository<Inventory, Long> {

    @Query(nativeQuery = true, value =
            "SELECT i.item_name as itemName, " +
                    "i.sales_qty as soldQuantity, " +
                    "CAST(i.sales_price AS DECIMAL(19, 2)) as salesPrice, " +
                    "CAST((i.sales_qty * i.sales_price) AS DECIMAL(19, 2)) as totalRevenue " +
                    "FROM inventory i " +
                    "WHERE i.sales_qty > 0 " +
                    "ORDER BY totalRevenue DESC")
    List<SalesRevenueReportBean> getSalesRevenueReport();
}

