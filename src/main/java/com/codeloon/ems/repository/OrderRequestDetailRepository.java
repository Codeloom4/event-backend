package com.codeloon.ems.repository;

import com.codeloon.ems.entity.OrderRequest;
import com.codeloon.ems.entity.OrderRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRequestDetailRepository extends JpaRepository <OrderRequestDetail,Integer>{

    List<OrderRequestDetail> findByorderId(OrderRequest orderId);
}
