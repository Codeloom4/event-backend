package com.codeloon.ems.service;

import com.codeloon.ems.model.SalesRevenueReportBean;
import com.codeloon.ems.repository.SalesRevenueReportRepository;
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
public class SalesRevenueReportService {

    private final SalesRevenueReportRepository salesRevenueReportRepository;

    public ResponseBean getSalesRevenueReportList() {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<SalesRevenueReportBean> reportList = salesRevenueReportRepository.getSalesRevenueReport();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Sales and revenue report retrieved successfully");
            responseBean.setContent(reportList);
        } catch (Exception e) {
            log.error("Error retrieving sales and revenue report", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error retrieving sales and revenue report");
        }
        return responseBean;
    }

    public byte[] generateSalesRevenueReportExcel() {
        try {
            List<SalesRevenueReportBean> reportList = salesRevenueReportRepository.getSalesRevenueReport();

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Sales and Revenue Report");
                Row headerRow = sheet.createRow(0);
                String[] columns = {"Item Name", "Sold Quantity", "Sales Price", "Total Revenue"};

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }

                int rowNum = 1;
                for (SalesRevenueReportBean item : reportList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(item.getItemName());
                    row.createCell(1).setCellValue(item.getSoldQuantity());
                    row.createCell(2).setCellValue(item.getSalesPrice().doubleValue());
                    row.createCell(3).setCellValue(item.getTotalRevenue().doubleValue());
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error generating sales and revenue report Excel", e);
            return new byte[0];
        }
    }

    public String getExcelFileName() {
        return "SalesRevenueReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
    }
}

