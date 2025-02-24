package com.codeloon.ems.service;

import com.codeloon.ems.dto.UserSummaryDto;
import com.codeloon.ems.dto.StockSummaryDto;
import com.codeloon.ems.dto.RevenueSummaryDto;
import com.codeloon.ems.repository.UserSummaryRepository;
import com.codeloon.ems.repository.StockSummaryRepository;
import com.codeloon.ems.repository.RevenueSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserSummaryRepository userSummaryRepository;
    private final StockSummaryRepository stockSummaryRepository;
    private final RevenueSummaryRepository revenueSummaryRepository;

    public List<UserSummaryDto> getUserSummary() {
        return userSummaryRepository.getUserSummary();
    }

    public List<StockSummaryDto> getStockSummary() {
        return stockSummaryRepository.getStockSummary();
    }

    public List<RevenueSummaryDto> getRevenueSummary() {
        return revenueSummaryRepository.getRevenueSummary();
    }
}

