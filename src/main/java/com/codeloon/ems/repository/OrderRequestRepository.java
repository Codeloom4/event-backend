package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.entity.OrderRequest;
import com.codeloon.ems.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest,String> {

    @Query("SELECT COUNT(o) FROM OrderRequest o ")
    int getOrderCount();

    @Query(" select c from OrderRequest c where c.customerUsername.username = ?1 ")
    Page<OrderRequest> findByCustomerUsername(String cusName, Pageable pageable);

    Page<OrderRequest> findAll(Pageable pageable);

    @Query(" select c from OrderRequest c where c.orderStatus.code = ?1 ")
    Page<OrderRequest> findAllByOrderStatus(String status, Pageable pageable);

    @Query(" select c from OrderRequest c where c.refStatus = ?1 ")
    Page<OrderRequest> findAllByRefStatus(String status, Pageable pageable);


    List<OrderRequest> findByRequestedDateBetweenAndOrderStatus(LocalDateTime startOfDay, LocalDateTime endOfDay, Status status);

    List<OrderRequest> findByRequestedDateBetweenAndPaymentStatus(LocalDateTime startOfDay, LocalDateTime endOfDay, Status status);
}
