package com.codeloon.ems.repository;

import com.codeloon.ems.entity.OrderRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRequestDetailRepository extends JpaRepository <OrderRequestDetail,Integer>{
}
