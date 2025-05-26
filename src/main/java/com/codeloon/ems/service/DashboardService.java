package com.codeloon.ems.service;

import com.codeloon.ems.dto.*;
import com.codeloon.ems.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserSummaryRepository userSummaryRepository;
    private final StockSummaryRepository stockSummaryRepository;
    private final OrderRequestRepository orderRequestRepository;
    private final PaymentRepository paymentRepository;

    public List<UserSummaryDto> getUserSummary() {
        return userSummaryRepository.getUserSummary();
    }

    public List<StockSummaryDto> getStockSummary() {
        return stockSummaryRepository.getStockSummary();
    }

    public PaymentStatsDto getPaymentStats() {
        PaymentStatsDto stats = new PaymentStatsDto();
        stats.setTotalRevenue(paymentRepository.getTotalRevenue());
        return stats;
    }

    public OrderStatsDto getOrderStats() {
        OrderStatsDto stats = new OrderStatsDto();
        stats.setCompletedOrders(orderRequestRepository.countCompletedOrders());
        return stats;
    }
}