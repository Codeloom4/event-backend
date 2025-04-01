package com.codeloon.ems.controller;

import com.codeloon.ems.dto.UserSummaryDto;
import com.codeloon.ems.dto.StockSummaryDto;
import com.codeloon.ems.dto.RevenueSummaryDto;
import com.codeloon.ems.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ems/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<UserSummaryDto>> getUserSummary() {
        return ResponseEntity.ok(dashboardService.getUserSummary());
    }

    @GetMapping("/stock-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<StockSummaryDto>> getStockSummary() {
        return ResponseEntity.ok(dashboardService.getStockSummary());
    }

    @GetMapping("/revenue-summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<RevenueSummaryDto>> getRevenueSummary() {
        return ResponseEntity.ok(dashboardService.getRevenueSummary());
    }
}

