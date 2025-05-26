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
            "SELECT r.cus_id as customerName, " +
                    "o.order_id as orderId, " +
                    "o.package_id as packageId, " +
                    "CAST(i.average_price AS DECIMAL(19, 2)) as averagePrice, " +
                    "r.quantity as quantity, " +
                    "i.item_name as itemName, " +
                    "i.category as category, " +
                    "CAST((i.average_price * r.quantity) AS DECIMAL(19, 2)) as totalRevenue " +
                    "FROM order_request o " +
                    "JOIN order_request_detail r ON o.order_id = r.order_id " +
                    "JOIN inventory_item i ON i.id = r.item_id " +
                    "WHERE o.payment_status = 'PAYMENT_APPROVED' AND i.category = 'Inventory' " +
                    "UNION ALL " +
                    "SELECT r.cus_id as customerName, " +
                    "o.order_id as orderId, " +
                    "o.package_id as packageId, " +
                    "CAST(i.average_price AS DECIMAL(19, 2)) as averagePrice, " +
                    "r.quantity as quantity, " +
                    "i.item_name as itemName, " +
                    "i.category as category, " +
                    "CAST(((i.average_price * 0.05) * r.quantity) AS DECIMAL(19, 2)) as totalRevenue " +
                    "FROM order_request o " +
                    "JOIN order_request_detail r ON o.order_id = r.order_id " +
                    "JOIN inventory_item i ON i.id = r.item_id " +
                    "WHERE o.payment_status = 'PAYMENT_APPROVED' AND i.category = 'Food' " +
                    "UNION ALL " +
                    "SELECT r.cus_id as customerName, " +
                    "o.order_id as orderId, " +
                    "o.package_id as packageId, " +
                    "CAST(i.average_price AS DECIMAL(19, 2)) as averagePrice, " +
                    "r.quantity as quantity, " +
                    "i.item_name as itemName, " +
                    "i.category as category, " +
                    "CAST((i.average_price * 0.05) AS DECIMAL(19, 2)) as totalRevenue " +
                    "FROM order_request o " +
                    "JOIN order_request_detail r ON o.order_id = r.order_id " +
                    "JOIN inventory_item i ON i.id = r.item_id " +
                    "WHERE o.payment_status = 'PAYMENT_APPROVED' AND i.category != 'Inventory'")
    List<SalesRevenueReportBean> getSalesRevenueReport();
}