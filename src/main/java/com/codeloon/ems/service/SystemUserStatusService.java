package com.codeloon.ems.service;

import com.codeloon.ems.model.SystemUserStatusBean;
import com.codeloon.ems.repository.SystemUserStatusRepository;
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
public class SystemUserStatusService {

    private final SystemUserStatusRepository systemUserStatusRepository;

    public ResponseBean getSystemUserStatusList() {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<SystemUserStatusBean> userStatusList = systemUserStatusRepository.findAllSystemUserStatus();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("System user status list retrieved successfully");
            responseBean.setContent(userStatusList);
        } catch (Exception e) {
            log.error("Error retrieving system user status list", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error retrieving system user status list");
        }
        return responseBean;
    }

    public byte[] generateSystemUserStatusExcel() {
        try {
            List<SystemUserStatusBean> userStatusList = systemUserStatusRepository.findAllSystemUserStatus();

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("System User Status");
                Row headerRow = sheet.createRow(0);
                String[] columns = {"User ID", "Username", "Email", "Position", "Mobile", "Address", "Enabled", "Account Non Expired",
                        "Credentials Non Expired", "Account Non Locked", "Force Password Change", "Created At"};

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }

                int rowNum = 1;
                for (SystemUserStatusBean user : userStatusList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(user.getUserId());
                    row.createCell(1).setCellValue(user.getUsername());
                    row.createCell(2).setCellValue(user.getEmail());
                    row.createCell(3).setCellValue(user.getPosition());
                    row.createCell(4).setCellValue(user.getMobile());
                    row.createCell(5).setCellValue(user.getAddress());
                    row.createCell(6).setCellValue(user.getEnabled() != null ? user.getEnabled() : false);
                    row.createCell(7).setCellValue(user.getAccountNonExpired() != null ? user.getAccountNonExpired() : false);
                    row.createCell(8).setCellValue(user.getCredentialsNonExpired() != null ? user.getCredentialsNonExpired() : false);
                    row.createCell(9).setCellValue(user.getAccountNonLocked() != null ? user.getAccountNonLocked() : false);
                    row.createCell(10).setCellValue(user.getForcePasswordChange() != null ? user.getForcePasswordChange() : false);
                    row.createCell(11).setCellValue(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error generating system user status Excel", e);
            return new byte[0];
        }
    }

    public String getExcelFileName() {
        return "SystemUserStatus_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
    }
}

