package com.codeloon.ems.controller;

import com.codeloon.ems.service.LowStockReportService;
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
@RequestMapping("/ems/reports/low-stock")
@RequiredArgsConstructor
public class LowStockReportController {

    private final LowStockReportService lowStockReportService;

    @GetMapping("/list")
    public ResponseEntity<?> getLowStockReportList() {
        return ResponseEntity.ok(lowStockReportService.getLowStockReportList());
    }

    @GetMapping("/generate-excel")
    public ResponseEntity<byte[]> generateLowStockReportExcel() {
        byte[] excelContent = lowStockReportService.generateLowStockReportExcel();
        String fileName = lowStockReportService.getExcelFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }
}

