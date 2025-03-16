package com.codeloon.ems.repository;

import com.codeloon.ems.entity.OrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest,String> {

    @Query("SELECT COUNT(o) FROM OrderRequest o ")
    int getOrderCount();
}
