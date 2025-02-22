package com.codeloon.ems.controller;

import com.codeloon.ems.service.InventoryStockReportService;
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
@RequestMapping("/ems/reports/inventory-stock")
@RequiredArgsConstructor
public class InventoryStockReportController {

    private final
    InventoryStockReportService inventoryStockReportService;

    @GetMapping("/list")
    public ResponseEntity<?> getInventoryStockReportList() {
        return ResponseEntity.ok(inventoryStockReportService.getInventoryStockReportList());
    }

    @GetMapping("/generate-excel")
    public ResponseEntity<byte[]> generateInventoryStockReportExcel() {
        byte[] excelContent = inventoryStockReportService.generateInventoryStockReportExcel();
        String fileName = inventoryStockReportService.getExcelFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }
}

