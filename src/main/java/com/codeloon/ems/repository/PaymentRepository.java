package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {  // Changed Long to String

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p ")
    Double getTotalRevenue();
}