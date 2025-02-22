package com.codeloon.ems.service;

import com.codeloon.ems.model.LowStockReportBean;
import com.codeloon.ems.repository.LowStockReportRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LowStockReportService {

    private final LowStockReportRepository lowStockReportRepository;

    @Value("${inventory.low-stock-threshold:10}")
    private int lowStockThreshold;

    public ResponseBean getLowStockReportList() {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<LowStockReportBean> reportList = lowStockReportRepository.getLowStockReport(lowStockThreshold);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Low stock report retrieved successfully");
            responseBean.setContent(reportList);
        } catch (Exception e) {
            log.error("Error retrieving low stock report", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error retrieving low stock report");
        }
        return responseBean;
    }

    public byte[] generateLowStockReportExcel() {
        try {
            List<LowStockReportBean> reportList = lowStockReportRepository.getLowStockReport(lowStockThreshold);

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Low Stock Report");
                Row headerRow = sheet.createRow(0);
                String[] columns = {"Item Name", "Available Quantity", "Ordered Quantity"};

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }

                int rowNum = 1;
                for (LowStockReportBean item : reportList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(item.getItemName());
                    row.createCell(1).setCellValue(item.getAvailableQuantity());
                    row.createCell(2).setCellValue(item.getOrderedQuantity());
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error generating low stock report Excel", e);
            return new byte[0];
        }
    }

    public String getExcelFileName() {
        return "LowStockReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
    }
}

