package com.codeloon.ems.controller;

import com.codeloon.ems.service.SalesRevenueReportService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ems/reports/sales-revenue")
@RequiredArgsConstructor
public class SalesRevenueReportController {

    private final SalesRevenueReportService salesRevenueReportService;

    @GetMapping("/list")
    public ResponseEntity<?> getSalesRevenueReportList() {
        return ResponseEntity.ok(salesRevenueReportService.getSalesRevenueReportList());
    }

    @GetMapping("/generate-excel")
    public ResponseEntity<byte[]> generateSalesRevenueReportExcel() {
        byte[] excelContent = salesRevenueReportService.generateSalesRevenueReportExcel();
        String fileName = salesRevenueReportService.getExcelFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }
}

