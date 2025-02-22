package com.codeloon.ems.service;

import com.codeloon.ems.model.InventoryStockReportBean;
import com.codeloon.ems.repository.InventoryStockReportRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryStockReportService {

    private final InventoryStockReportRepository inventoryStockReportRepository;

    public ResponseBean getInventoryStockReportList() {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<InventoryStockReportBean> reportList = inventoryStockReportRepository.getInventoryStockReport();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Inventory stock report retrieved successfully");
            responseBean.setContent(reportList);
        } catch (Exception e) {
            log.error("Error retrieving inventory stock report", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error retrieving inventory stock report");
        }
        return responseBean;
    }

    public byte[] generateInventoryStockReportExcel() {
        try {
            List<InventoryStockReportBean> reportList = inventoryStockReportRepository.getInventoryStockReport();

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Inventory Stock Report");
                Row headerRow = sheet.createRow(0);
                String[] columns = {"Item Name", "Available Quantity", "Ordered Quantity", "Sold Quantity", "Is Refundable"};

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }

                int rowNum = 1;
                for (InventoryStockReportBean item : reportList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(item.getItemName());
                    row.createCell(1).setCellValue(item.getAvailableQuantity());
                    row.createCell(2).setCellValue(item.getOrderedQuantity());
                    row.createCell(3).setCellValue(item.getSoldQuantity());
                    row.createCell(4).setCellValue(item.getIsRefundable() != null ? item.getIsRefundable().toString() : "");
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error generating inventory stock report Excel", e);
            return new byte[0];
        }
    }

    public String getExcelFileName() {
        return "InventoryStockReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
    }
}

